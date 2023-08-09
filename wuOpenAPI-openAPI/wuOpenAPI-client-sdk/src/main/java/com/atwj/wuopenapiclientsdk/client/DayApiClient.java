package com.atwj.wuopenapiclientsdk.client;

import cn.hutool.http.HttpRequest;

/**
 * DayController-DayApiClient
 */
public class DayApiClient extends CommonClient{

    public DayApiClient(String accessKey, String secretKey) {
        super(accessKey, secretKey);
    }

    /**
     * 获取每日壁纸
     * @return
     */
    public String getDayWallpaperUrl(){
        return HttpRequest.post(GATEWAY_PORT+"/api/day/wallpaper")
                .addHeaders(getHeaderMap(""))
                .execute().body();
    }
}
