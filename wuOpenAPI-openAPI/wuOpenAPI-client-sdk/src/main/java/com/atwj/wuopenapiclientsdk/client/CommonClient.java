package com.atwj.wuopenapiclientsdk.client;

import cn.hutool.core.util.RandomUtil;
import com.atwj.wuopenapiclientsdk.utils.SignUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 调用第三方API的通用客户端类
 */
public class CommonClient {

    private final String accessKey;
    private final String secreteKey;

    protected static final String GATEWAY_PORT = "http://localhost:8090/";

    public CommonClient(String accessKey, String secreteKey) {
        this.accessKey = accessKey;
        this.secreteKey = secreteKey;
    }

    /**
     * 拼接请求头
     * @param body
     * @return
     */
    public Map<String, String> getHeaderMap(String body) {
        Map<String, String> map = new HashMap<>();
        map.put("accessKey", accessKey);
        map.put("nonce", RandomUtil.randomNumbers(1000));
        map.put("body", body);
        map.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
        map.put("sign", SignUtil.getSign(body, secreteKey));
        return map;
    }

}
