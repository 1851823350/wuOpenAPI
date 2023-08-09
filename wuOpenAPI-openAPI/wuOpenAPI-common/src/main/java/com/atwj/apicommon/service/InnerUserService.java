package com.atwj.apicommon.service;


import com.atwj.apicommon.model.entity.User;

/**
 * 用户服务
 *
 * @author wj
 */
public interface InnerUserService{

    /**
     * 查询数据库是否为用户分配 密钥
     * @author 吴先森
     * @date 2023/1/14 15:59
     */

    User getInvokeUser(String accessKey);

}
