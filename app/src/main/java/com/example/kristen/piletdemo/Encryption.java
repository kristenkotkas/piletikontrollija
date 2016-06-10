package com.example.kristen.piletdemo;

import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Encryption {
    private static byte[] secret;

    public static void setSecret(String data) {
        secret = Base64.decode(data.split(" ")[1], Base64.DEFAULT);
    }

    public static String decrypt(String encrypted) {
        try {
            byte[] ivbytes = Base64.decode(encrypted.split(";")[0], Base64.DEFAULT);
            byte[] encryptedBytes = Base64.decode(encrypted.split(";")[1], Base64.DEFAULT);
            SecretKeySpec sks = new SecretKeySpec(secret, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, sks, new IvParameterSpec(ivbytes));
            return new String(cipher.doFinal(encryptedBytes));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
