package com.atwj.wuopenapiclientsdk.client;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.atwj.wuopenapiclientsdk.model.TestUser;
import com.atwj.wuopenapiclientsdk.utils.SignUtil;


import java.util.HashMap;
import java.util.Map;

/**
 * 测试姓名接口
 */
public class WuApiClient extends CommonClient{

    public WuApiClient(String accessKey, String secreteKey) {
        super(accessKey, secreteKey);
    }


    public String getUserNameByPost(TestUser user) {
        String json = JSONUtil.toJsonStr(user);
        String result2 = HttpRequest.post(GATEWAY_PORT + "api/name/post/user")
                .body(json)
                .addHeaders(getHeaderMap(json))
                .execute().body();
        return result2;
    }
}
