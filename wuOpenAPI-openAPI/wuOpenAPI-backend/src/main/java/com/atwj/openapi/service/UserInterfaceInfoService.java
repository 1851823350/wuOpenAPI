package com.atwj.openapi.service;


import com.atwj.apicommon.model.entity.UserInterfaceInfo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author blablalalala
* @description 针对表【user_interface_info(用户调用接口关系表)】的数据库操作Service
* @createDate 2022-12-06 09:36:29
*/
public interface UserInterfaceInfoService extends IService<UserInterfaceInfo> {

    /**
     * 用户接口信息校验
     * @param userInterfaceInfo
     * @param add
     */
    void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean add);

    /**
     * 统计接口调用次数
     * @param interfaceInfoId
     * @param userId
     * @return
     */
    boolean invokeCount(long interfaceInfoId,long userId);

    /**
     * 回滚调用次数
     * @param userId
     * @param interfaceInfoId
     * @return
     */
    boolean recoverInvokeCount(Long userId, Long interfaceInfoId);
}
