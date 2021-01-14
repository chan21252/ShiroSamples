package com.chan.shiro.common;

import org.apache.shiro.crypto.hash.Md5Hash;

/**
 * MD5Util
 *
 * @author Chan
 * @since 2021/1/13
 */
public class MD5Util {
    //公盐
    private final static String PUBLIC_SALT = "a7dbe4738dfd450f81008a11a17e210f";

    //散列次数
    private final static int HASH_ITERATIONS = 3;

    /**
     * md5加密
     *
     * @param source    原始次数
     * @return          MD5摘要
     */
    public static String md5(String source) {
        return new Md5Hash(source).toHex();
    }

    /**
     * 原始数据，加上公盐加密
     *
     * @param source    原始数据
     * @return          公盐加密后的数据
     */
    private static String md5PublicSalt(String source) {
        return new Md5Hash(source, PUBLIC_SALT, HASH_ITERATIONS).toHex();
    }

    /**
     * 原始数据先用公盐加密，加上私盐加密
     *
     * @param source    原始数据
     * @param salt      私盐
     * @return          私盐加密后的数据
     */
    public static String md5privateSalt(String source, String salt) {
        return new Md5Hash(md5PublicSalt(source), salt, HASH_ITERATIONS).toHex();
    }
}