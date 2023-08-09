package com.atwj.wuopenapiclientsdk.client;

import cn.hutool.http.HttpRequest;
/**
 * RandomController-RandomApiClient
 */
public class RandomApiClient extends CommonClient{

    public RandomApiClient(String accessKey, String secretKey) {
        super(accessKey, secretKey);
    }

    /**
     * 获取随机文本
     * @return
     */
    public String getRandomWork(){
        return HttpRequest.get(GATEWAY_PORT+"api/random/word")
                .addHeaders(getHeaderMap(""))
                .execute().body();
    }

    /**
     * 获取随机动漫图片
     * @return
     */
    public String getRandomImageUrl(){
        return HttpRequest.post(GATEWAY_PORT+"api/random/image")
                .addHeaders(getHeaderMap(""))
                .execute().body();
    }
}
