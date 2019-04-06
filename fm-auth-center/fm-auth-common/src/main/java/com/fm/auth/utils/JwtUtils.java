package com.fm.auth.utils;

import com.fm.auth.entity.JwtConstants;
import com.fm.auth.entity.UserInfo;
import io.jsonwebtoken.*;
import org.joda.time.DateTime;
import java.security.PrivateKey;
import java.security.PublicKey;

public class JwtUtils {
    /**
     * 私钥加密token，生成Token
     *
     * @param userInfo      载荷中的数据
     * @param privateKey    私钥
     * @param expireMinutes 过期时间，单位秒
     * @return
     */
    public static String generateToken(UserInfo userInfo, PrivateKey privateKey, int expireMinutes) {
        return Jwts.builder()
                .claim(JwtConstants.JWT_KEY_ID, userInfo.getId())
                .claim(JwtConstants.JWT_KEY_USER_NAME, userInfo.getUsername())
                .setExpiration(DateTime.now().plusMinutes(expireMinutes).toDate())
                .signWith(SignatureAlgorithm.RS256, privateKey)
                .compact();
    }

    /**
     * 私钥加密token，生成Token
     *
     * @param userInfo      载荷中的数据
     * @param privateKey    私钥字节数组
     * @param expireMinutes 过期时间，单位秒
     * @return
     * @throws Exception
     */
    public static String generateToken(UserInfo userInfo, byte[] privateKey, int expireMinutes) throws Exception {
        return Jwts.builder()
                .claim(JwtConstants.JWT_KEY_ID, userInfo.getId())
                .claim(JwtConstants.JWT_KEY_USER_NAME, userInfo.getUsername())
                .setExpiration(DateTime.now().plus(expireMinutes).toDate())
                .signWith(SignatureAlgorithm.ES256, RsaUtils.getPrivateKey(privateKey))
                .compact();
    }

    /**
     * 公钥解析Token
     *
     * @param publicKey 公钥
     * @param token     用户请求中的token
     * @return
     */
    public static Jws<Claims> parseToken(PublicKey publicKey, String token) {
        return Jwts.parser().setSigningKey(publicKey).parseClaimsJws(token);
    }


    /**
     * 公钥解析Token
     *
     * @param publicKey 公钥字节数组
     * @param token     用户请求中的token
     * @return
     * @throws Exception
     */
    public static Jws<Claims> parseToken(byte[] publicKey, String token) throws Exception {
        return Jwts.parser().setSigningKey(RsaUtils.getPublicKey(publicKey)).parseClaimsJws(token);
    }


    /**
     * 从Token中获取用户信息（使用公钥解析）
     *
     * @param publicKey 公钥
     * @param token     用户请求中的令牌
     * @return 用户信息
     */
    public static UserInfo getUserInfo(PublicKey publicKey, String token) {
        Jws<Claims> claimsJws = parseToken(publicKey, token);
        Claims body = claimsJws.getBody();
        return new UserInfo(
                ObjectUtils.toLong(body.get(JwtConstants.JWT_KEY_ID)),
                ObjectUtils.toString(body.get(JwtConstants.JWT_KEY_USER_NAME))
        );
    }

    /**
     * 从Token中获取用户信息（使用公钥解析）
     *
     * @param publicKey 公钥
     * @param token     用户请求中的令牌
     * @return 用户信息
     * @throws Exception
     */
    public static UserInfo getUserInfo(byte[] publicKey, String token) throws Exception {
        Jws<Claims> claimsJws = parseToken(publicKey, token);
        Claims body = claimsJws.getBody();
        return new UserInfo(
                ObjectUtils.toLong(body.get(JwtConstants.JWT_KEY_ID)),
                ObjectUtils.toString(body.get(JwtConstants.JWT_KEY_USER_NAME))
        );
    }
}
