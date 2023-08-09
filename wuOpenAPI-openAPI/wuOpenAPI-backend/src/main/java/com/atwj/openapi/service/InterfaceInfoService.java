package com.atwj.openapi.service;


import com.atwj.apicommon.model.entity.InterfaceInfo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author blablablala
* @description 针对表【interface_info(接口信息)】的数据库操作Service
* @createDate 2023-07-26 20:11:46
*/
public interface InterfaceInfoService extends IService<InterfaceInfo> {

    //校验接口信息
    void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add);

}
