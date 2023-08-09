package com.atwj.openapi.service.innerImpl.inner;


import com.atwj.apicommon.model.entity.UserInterfaceInfo;
import com.atwj.apicommon.service.InnerUserInterfaceInfoService;
import com.atwj.openapi.common.ErrorCode;
import com.atwj.openapi.exception.BusinessException;
import com.atwj.openapi.service.UserInterfaceInfoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

@DubboService
public class InnerUserInterfaceInfoServiceImpl implements InnerUserInterfaceInfoService {

    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;

    @Override
    public Boolean invokeCount(long interfaceInfoId, long userId) {
        return userInterfaceInfoService.invokeCount(interfaceInfoId, userId);
    }

    @Override
    public boolean validLeftNum(Long userId, Long interfaceInfoId) {
        LambdaQueryWrapper<UserInterfaceInfo> lqw = new LambdaQueryWrapper<>();
        lqw.eq(UserInterfaceInfo::getUserId, userId)
                .eq(UserInterfaceInfo::getInterfaceInfoId, interfaceInfoId);
        UserInterfaceInfo userInterfaceInfo = userInterfaceInfoService.getOne(lqw);
        if (userInterfaceInfo == null || userInterfaceInfo.getLeftNum() <= 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        return true;
    }
}
