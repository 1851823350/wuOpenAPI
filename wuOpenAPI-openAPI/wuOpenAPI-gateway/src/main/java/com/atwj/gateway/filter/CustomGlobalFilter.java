package com.atwj.gateway.filter;

import com.atwj.apicommon.model.entity.InterfaceInfo;
import com.atwj.apicommon.model.entity.User;
import com.atwj.apicommon.model.entity.UserInterfaceInfoMessage;
import com.atwj.apicommon.service.InnerInterfaceInfoService;
import com.atwj.apicommon.service.InnerUserInterfaceInfoService;
import com.atwj.apicommon.service.InnerUserService;
import com.atwj.gateway.manager.RedisLimiterManager;
import com.atwj.gateway.rabbitmq.APIMessageProducer;
import com.atwj.wuopenapiclientsdk.utils.SignUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * 全局过滤
 */
@Slf4j
@Component
public class CustomGlobalFilter implements GlobalFilter, Ordered {
    @DubboReference
    private InnerInterfaceInfoService interfaceInfoService;

    @DubboReference
    private InnerUserInterfaceInfoService innerUserInterfaceInfoService;

    @DubboReference
    private InnerUserService innerUserService;

    @Resource
    private RedisLimiterManager redisLimiterManager;

    @Resource
    private APIMessageProducer apiMessageProducer;


    private static final String INTERFACE_HOST = "http://localhost:8123";


    /**
     * 全局过滤
     *
     * @param exchange
     * @param chain
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //1.请求日志
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        String path = INTERFACE_HOST + request.getPath().value();
        String method = request.getMethod().toString();
        log.info("请求唯一标识：" + request.getId());
        log.info("请求地址：" + path);
        log.info("请求方法：" + request.getMethod());
        log.info("请求来源地址：" + request.getLocalAddress().getHostString());
        log.info("请求参数：" + request.getQueryParams());

        //2.用户鉴权
        HttpHeaders headers = request.getHeaders();
        String accessKey = headers.getFirst("accessKey");
        String nonce = headers.getFirst("nonce");
        String timestamp = headers.getFirst("timestamp");
        String sign = headers.getFirst("sign");
        String body = headers.getFirst("body");

        long currentTime = System.currentTimeMillis() / 1000;
        final long FIVE_MINUTES = 60 * 5L;
        if ((currentTime - Long.parseLong(timestamp)) >= FIVE_MINUTES) {
            return handleNoAuth(response);
        }
        //数据库查询accessKey和进行签名认证
        User invokeUser = innerUserService.getInvokeUser(accessKey);
        if (invokeUser == null) {
            return handleNoAuth(response);
        }
        String secretKey = invokeUser.getSecretKey();
        String serverSign = SignUtil.getSign(body, secretKey);
        if (!serverSign.equals(sign)) {
            return handleNoAuth(response);
        }
        //3.请求接口是否存在
        InterfaceInfo interfaceInfo = null;
        try {
            interfaceInfo = interfaceInfoService.getInterfaceInfo(path, method);
        } catch (Exception e) {
            log.error("interfaceInfo error", e);
        }

        //为每一个用户分配一个限流器，进行限流
        redisLimiterManager.doRateLimit("invokeAPI_" + accessKey);

        //4.统计接口调用次数以及判断是否有剩余次数
        Boolean aBoolean = null;
        try {
            aBoolean = innerUserInterfaceInfoService.invokeCount(interfaceInfo.getId(), invokeUser.getId());
        } catch (Exception e) {
            log.info("统计接口失败或者有人恶意调用不存在的接口");
            e.printStackTrace();
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
            return response.setComplete();
        }
        if (!aBoolean) {
            log.info("调用接口次数不足");
            return handleNoAuth(response);
        }

        //4.相应日志+调用模拟接口
        return handleResponse(exchange, chain, interfaceInfo.getId(), invokeUser.getId());
    }

    /**
     * 处理响应
     *
     * @param exchange
     * @param chain
     * @return
     */
    public Mono<Void> handleResponse(ServerWebExchange exchange, GatewayFilterChain chain, long interfaceInfoId, long userId) {
        try {
            ServerHttpResponse originalResponse = exchange.getResponse();
            //缓冲区工厂
            DataBufferFactory bufferFactory = originalResponse.bufferFactory();
            //拿到响应码
            HttpStatus statusCode = originalResponse.getStatusCode();
            if (statusCode == HttpStatus.OK) {
                //装饰，增强能力
                ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
                    //等调用完转发的接口后才会执行
                    @Override
                    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                        //log.info("body instanceof Flux: {}", (body instanceof Flux));
                        if (body instanceof Flux) {
                            Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                            //往返回值里面写数据
                            //拼接字符串
                            return super.writeWith(
                                    fluxBody.map(dataBuffer -> {


                                        byte[] content = new byte[dataBuffer.readableByteCount()];
                                        dataBuffer.read(content);
                                        DataBufferUtils.release(dataBuffer);//释放掉内存
                                        // 获取响应结果，构建日志
                                        log.info("接口调用响应码为：" + originalResponse.getStatusCode());

                                        //5.接口调用失败，利用消息队列实现接口统计数据的回滚；因为消息队列的可靠性所以我们选择消息队列而不是远程调用来实现
                                        if (!(originalResponse.getStatusCode() == HttpStatus.OK)) {
                                            UserInterfaceInfoMessage vo = new UserInterfaceInfoMessage();
                                            vo.setUserId(userId);
                                            vo.setInterfaceInfoId(interfaceInfoId);
                                            apiMessageProducer.sendMessage(vo);
                                        }
                                        //打印日志
                                        return bufferFactory.wrap(content);
                                    }));
                        } else {
                            log.error("<--- {} 响应code异常", getStatusCode());
                        }
                        return super.writeWith(body);
                    }
                };
                //设置response对象为装饰过的
                return chain.filter(exchange.mutate().response(decoratedResponse).build());
            }
            return chain.filter(exchange);//降级处理返回数据
        } catch (Exception e) {
            log.error("网关处理响应错误.\n" + e);
            return chain.filter(exchange);
        }
    }

    @Override
    public int getOrder() {
        return -1;
    }

    public Mono<Void> handleNoAuth(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.FORBIDDEN);
        return response.setComplete();
    }

    public Mono<Void> handleInvokeError(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        return response.setComplete();
    }
}
