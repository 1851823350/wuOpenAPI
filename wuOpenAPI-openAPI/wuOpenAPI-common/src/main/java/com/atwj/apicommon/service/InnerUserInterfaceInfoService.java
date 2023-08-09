package com.atwj.apicommon.service;

/**
 * @author 吴先森
 * @description:
 * @create 2023-01-14 15:59
 */
public interface InnerUserInterfaceInfoService {


    /*
     * 调用接口统计次数
     * @author 吴先森
     * @date 2023/1/14 16:01
     */
    Boolean invokeCount(long interfaceInfoId, long userId);

    /**
     * 判断剩余调用次数
     *
     * @param userId
     * @param interfaceInfoId
     * @return
     */
    boolean validLeftNum(Long userId, Long interfaceInfoId);
}
