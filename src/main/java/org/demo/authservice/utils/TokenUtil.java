package org.demo.authservice.utils;

import org.demo.authservice.entity.Session;
import org.demo.authservice.entity.User;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Zhi Chen
 * @date 2022/8/27
 * @description to generate and validate token for the user
 */
public class TokenUtil {

    private static long expiredTime = 2 * 60 * 60 * 1000;

    private static final Map<String, User> tokenUserMap = new ConcurrentHashMap<>();
    private static final Map<String, Long> tokenTimeMap = new ConcurrentHashMap<>();
    private static final Map<String, Session> tokenMap = new ConcurrentHashMap<>();

    public static void setExpiredTime(long expiredTime) {
        TokenUtil.expiredTime = expiredTime;
    }

    /**
     * generate user's token and store associated information in session
     * @param user the user to authenticate
     * @return token of user
     */
    public static String generateToken(User user) {
        long currentTime = System.currentTimeMillis();
        String token = generateUUID() + currentTime;
        Session session = new Session(user,  currentTime);
        tokenMap.put(token, session);
        return token;
    }

    /**
     * remove expired token
     */
    public static void removeExpiredToken() {
         for (Map.Entry<String, Session> entry : tokenMap.entrySet()) {
             if (System.currentTimeMillis() - entry.getValue().getTime() >= expiredTime) {
                 tokenMap.remove(entry.getKey());
             }
         }
    }

    /**
     * validate the token
     * @param token
     * @return
     */
    public static boolean validateToken(String token) {
        if (!tokenMap.containsKey(token)) {
            return false;
        }
        if (System.currentTimeMillis() - tokenMap.get(token).getTime() >= expiredTime) {
            tokenMap.remove(token);
            return false;
        }
        return true;
    }

    /**
     * remove the token to make it invalid for client
     * @param token
     */
    public static void invalidateToken(String token) {
            tokenMap.remove(token);
    }


    /**
     * return the associated user with token
     */
    public static User getUserByToken(String token) {
        return tokenMap.get(token).getUser();
    }

    /**
     * update token expiring time if client uses the token recently
     * @param token
     */
    public static void updateTokenTime(String token) {
        if (validateToken(token)) {
            tokenMap.get(token).setTime(System.currentTimeMillis());
        }
    }

    private static String generateUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

}

