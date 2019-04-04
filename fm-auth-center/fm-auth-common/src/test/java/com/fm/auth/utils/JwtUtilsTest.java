package com.fm.auth.utils;


import com.fm.auth.entity.UserInfo;
import org.junit.Before;
import org.junit.Test;

import java.security.PrivateKey;
import java.security.PublicKey;


public class JwtUtilsTest {

    private static final String publicKeyPath = "D:\\FitnessMall\\rsa.pub";
    private static final String privateKeyPath = "D:\\FitnessMall\\rsa.pri";

    private PrivateKey privateKey;
    private PublicKey publicKey;


    @Test
    public void testRsa() throws Exception {
        RsaUtils.generateKey(publicKeyPath, privateKeyPath, "234");
    }

    @Before
    public void testGetRsa() throws Exception {
        privateKey = RsaUtils.getPrivateKey(privateKeyPath);
        publicKey = RsaUtils.getPublicKey(publicKeyPath);
    }

    @org.junit.Test
    public void generateToken() {
        //生成Token
        String s = JwtUtils.generateToken(new UserInfo(20L, "Jack"), privateKey, 5);
        System.out.println("s = " + s);
    }


    @org.junit.Test
    public void parseToken() {
        String token = "eyJhbGciOiJSUzI1NiJ9.eyJpZCI6MjAsInVzZXJuYW1lIjoiSmFjayIsImV4cCI6MTU1NDM4MzIyOX0.J123-kPidRp5CD3hUslVdPU5djN9qaURN47-ILKw_2OOL_JiFRbjbrAlmhbQbYdxVpgbIGQTYYnUrAXtsT0JI-G_CwJAqPx11EUhFjs1KMfXKEUqWdpe5G8Fbi58Za2_TPaq7ms-xisPqLOwXr8SnLC9lBcpj2Y0OnsvZVfkjMw";
        UserInfo userInfo = JwtUtils.getUserInfo(publicKey, token);
        System.out.println("id:" + userInfo.getId());
        System.out.println("name:" + userInfo.getName());
    }

    @org.junit.Test
    public void parseToken1() {
    }

    @org.junit.Test
    public void getUserInfo() {
    }

    @org.junit.Test
    public void getUserInfo1() {
    }
}