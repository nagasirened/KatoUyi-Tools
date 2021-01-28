package com.katouyi.tools.redis.prefix;

/**
 * <p>
 *
 * @description: 认证用户信息
 * </p>
 * @author: ZengGuangfu
 */
public class StaticPrefix extends BasePrefix{

    public StaticPrefix(int expireTime, String prefixStr){
        super(expireTime, prefixStr);
    }

    public StaticPrefix(String prefixStr){
        super(-1, prefixStr);
    }

    public static StaticPrefix BANNERS = new StaticPrefix("allBanners");
    public static StaticPrefix ROOTCATEGORY = new StaticPrefix("rootCategory");
    public static StaticPrefix SUBCATEGORY = new StaticPrefix("subCategory:");
    public static StaticPrefix SHOPCART = new StaticPrefix("shopcart:");
    public static StaticPrefix CAS_USER_SESSION = new StaticPrefix(604800, "cas_user_session:");
    public static StaticPrefix LOGIN_EXPIRE = new StaticPrefix(604800, "login_with_expire:");
    public static StaticPrefix USER_TIECKET = new StaticPrefix(604800, "user_ticket:");
    public static StaticPrefix TMP_TIECKET = new StaticPrefix(300, "tmp_ticket:");
    public static StaticPrefix REIDS_NX_TEST = new StaticPrefix(300, "redisnx:");
}
