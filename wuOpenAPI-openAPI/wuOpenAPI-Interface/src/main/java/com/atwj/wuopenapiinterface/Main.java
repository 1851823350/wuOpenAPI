package com.atwj.wuopenapiinterface;

import com.atwj.wuopenapiclientsdk.client.WuApiClient;
import com.atwj.wuopenapiclientsdk.model.TestUser;

public class Main {
    public static void main(String[] args) {
        WuApiClient wuApiClient = new WuApiClient("wjTest", "wjTest");
        TestUser testUser = new TestUser();
        testUser.setUsername("wj");
        String res3 = wuApiClient.getUserNameByPost(testUser);
        System.out.println(res3);

    }
}
