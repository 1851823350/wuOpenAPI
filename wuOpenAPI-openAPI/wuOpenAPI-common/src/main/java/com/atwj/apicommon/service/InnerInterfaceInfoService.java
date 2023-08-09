package com.atwj.apicommon.service;


import com.atwj.apicommon.model.entity.InterfaceInfo;

/**
 *
 */
public interface InnerInterfaceInfoService {

    /*
     * 查询接口是否存在
     * @author 吴先森
     * @date 2023/1/14 16:01
     */
    InterfaceInfo getInterfaceInfo(String path, String method);
}
