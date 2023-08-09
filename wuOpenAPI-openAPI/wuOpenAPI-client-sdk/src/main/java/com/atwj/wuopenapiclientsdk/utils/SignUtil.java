package com.atwj.wuopenapiclientsdk.utils;

import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;

public class SignUtil {
    /**
     * 加密secretKey，生成签名
     * @param map
     * @param secreteKey
     * @return
     */
    public static String getSign(String body, String secreteKey) {
        Digester md5 = new Digester(DigestAlgorithm.SHA256);
        String content = body + "." + secreteKey;
        return md5.digestHex(content);
    }
}
