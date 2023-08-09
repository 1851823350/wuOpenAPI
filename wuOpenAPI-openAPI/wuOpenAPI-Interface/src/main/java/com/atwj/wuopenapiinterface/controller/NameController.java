package com.atwj.wuopenapiinterface.controller;


import com.atwj.wuopenapiclientsdk.model.TestUser;
import com.atwj.wuopenapiclientsdk.utils.SignUtil;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 模拟接口：查询名称
 */
@RestController
@RequestMapping("/name")
public class NameController {

    @GetMapping("/get")
    public String getNameByGet(String name, HttpServletRequest request) {
        return "GET方法得到的名称为：" + name;
    }

    @PostMapping("/post")
    public String getNameByPost(@RequestParam String name) {
        return "POST方法得到的名称为：" + name;
    }

    @PostMapping("/post/user")
    public String getUserNameByPost(@RequestBody TestUser user, HttpServletRequest request) {
        String accessKey = request.getHeader("accessKey");
        String nonce = request.getHeader("nonce");
        String timestamp = request.getHeader("timestamp");
        String sign = request.getHeader("sign");
        String body = request.getHeader("body");
        // todo 实际情况应该是去数据库中查是否已分配给用户
        if (!accessKey.equals("wjTest")) {
            throw new RuntimeException("无权限");
        }
//        if (Long.parseLong(nonce) > 10000) {
//            throw new RuntimeException("无权限");
//        }
//         todo 时间和当前时间不能超过 5 分钟
//        if (timestamp) {
//
//        }
//         todo 实际情况中是从数据库中查出 secretKey
        String serverSign = SignUtil.getSign(body, "wjTest");
        if (!sign.equals(serverSign)) {
            throw new RuntimeException("无权限");
        }
        return "POST方法得到的TestUser的名称为：" + user.getUsername();
    }
}
