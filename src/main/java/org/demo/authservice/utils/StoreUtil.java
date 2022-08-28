package org.demo.authservice.utils;

import org.demo.authservice.entity.Role;
import org.demo.authservice.entity.User;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * @author Zhi Chen
 * @date 2022/8/27
 * @description Memory store for users and roles
 */
public class StoreUtil {

    private static Map<String, User> usersStore = new HashMap<>();

    private static Set<Role> roleStore = new HashSet<>();

    public static boolean containsUser(String userName) {
        return usersStore.containsKey(userName);
    }

    public static User storeUser(User user) {
        return usersStore.put(user.getUserName(), user);
    }

    public static User getUser(String userName) {
        return usersStore.get(userName);
    }

    public static boolean removeUser(User user) {
       usersStore.remove(user.getUserName());
       return true;
    }

    public static boolean storeRole(Role role) {
        return roleStore.add(role);
    }

    public static boolean removeRole(Role role) {
        return roleStore.remove(role);
    }

    public static boolean existRole(Role role) {
        return roleStore.contains(role);
    }

}
