package com.atwj.gateway.manager;

import com.atwj.apicommon.exception.BusinessException;
import com.atwj.apicommon.exception.ErrorCode;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 专门提供Redisson的限流服务
 *
 * @author blablalala
 */
@Service
public class RedisLimiterManager {
    @Resource
    private RedissonClient redissonClient;

    /**
     * 限流器
     * @param key key 区分不同的限流器，比如不同的用户 accesskey 应该分别统计
     */
    public void doRateLimit(String key) {
        // 创建一个名称为user_limiter的限流器，每秒最多访问 2 次
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(key);
        rateLimiter.trySetRate(RateType.OVERALL, 2, 1, RateIntervalUnit.SECONDS);
        // 每当一个操作来了后，请求一个令牌
        boolean canOp = rateLimiter.tryAcquire(1);
        if (!canOp) {
            /**
             * 当用户请求到达时，如果无法获取到令牌，表明请求次数达到上限进行限流处理（无法获取到令牌），抛异常
             */
            throw new BusinessException(ErrorCode.TOO_MANY_REQUEST);
        }
    }
}
