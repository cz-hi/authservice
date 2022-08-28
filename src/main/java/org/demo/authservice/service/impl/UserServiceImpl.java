package org.demo.authservice.service.impl;

import org.demo.authservice.entity.Role;
import org.demo.authservice.entity.User;
import org.demo.authservice.service.IUserService;
import org.demo.authservice.utils.EncryptUtil;
import org.demo.authservice.utils.StoreUtil;
import org.demo.authservice.utils.TokenUtil;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Zhi Chen
 * @date 2022/8/27
 * @description
 */
public class UserServiceImpl implements IUserService {

    private ScheduledExecutorService executor;

    public UserServiceImpl() {
        this.executor = Executors.newScheduledThreadPool(5);
    }

    /**
     * clear expired tokens every 2 hours to avoid too many expired tokens in memory
     */
    public void start() {
        executor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                TokenUtil.removeExpiredToken();
            }
        }, 2 , 2, TimeUnit.HOURS);
    }

    public void destroy() {
        executor.shutdownNow();
    }

    /**
     * create user
     * @param userName the unique identifier of user
     * @param password user's password
     * @return true if create user successfully,else false
     */
    public boolean createUser(String userName, String password) {
        if (StoreUtil.containsUser(userName)) {
            return false;
        }
        String encryptedPassword = EncryptUtil.encryptPassword(password);
        User user = new User(userName, encryptedPassword);
        StoreUtil.storeUser(user);
        return true;
    }

    /**
     * delete user
     * @param user the user to delete
     * @return true if the user exists,else false
     */
    public boolean deleteUser(User user) {
        if (!StoreUtil.containsUser(user.getUserName())) {
            return false;
        }
        StoreUtil.removeUser(user);
        return true;
    }

    /**
     * create role
     * @param roleName the name of role
     * @return true if role not exist, else false
     */
    public boolean createRole(String roleName) {
        return StoreUtil.storeRole(new Role(roleName));
    }

    /**
     * delete role
     * @param role the role to delete
     * @return true if the role exists,else false
     */
    public boolean deleteRole(Role role) {
        return StoreUtil.removeRole(role);
    }

    /**
     * add role to a user,the user and role must exist
     * @param user add role to the user
     * @param role the role to be added to user
     */
    public void addRoleToUser(User user, Role role) {
        if (!StoreUtil.containsUser(user.getUserName())) {
            return;
        }
        if (!StoreUtil.existRole(role)) {
            return;
        }
        User storedUser = StoreUtil.getUser(user.getUserName());
        List<Role> roles = storedUser.getRoles();
        if (roles.contains(role)) {
            return;
        }
        roles.add(role);
        storedUser.setRoles(roles);
        StoreUtil.storeUser(storedUser);
    }

    /**
     * authenticate user
     * @param userName the userName to be authenticated
     * @param password the password to be authenticated
     * @return the token if the userName and password are found
     * @throws RuntimeException will be thrown if user is not exist or password is not right
     */
    public String authenticateUser(String userName, String password) {
        if (!StoreUtil.containsUser(userName)) {
            throw new RuntimeException("user is not exist");
        }
        User user = StoreUtil.getUser(userName);
        if (!EncryptUtil.encryptPassword(password).equals(user.getPassword())) {
            throw new RuntimeException("password is not right");
        }
        return TokenUtil.generateToken(user);
    }

    /**
     * invalidate user's token
     * @param token the token to be invalidated
     */
    public void invalidateToken(String token) {
        if (!TokenUtil.validateToken(token)) {
            return;
        }
        TokenUtil.invalidateToken(token);
    }

    /**
     * check role of user
     * @param token the user's token
     * @param role the role to be checked
     * @return true if the user identified by the token,belongs to the role,else false
     * @throws RuntimeException will be thrown if the token is invalid,expired etc
     */
    public boolean checkRole(String token, Role role) {
        if (!TokenUtil.validateToken(token)) {
            throw new RuntimeException("token is not valid");
        }
        User user = TokenUtil.getUserByToken(token);
        // update the token expiring time
        TokenUtil.updateTokenTime(token);
        return user.hasRole(role);
    }

    /**
     * get all of user's roles
     * @param token the user's token
     * @return list contains all roles of the user
     * @throws RuntimeException will be thrown if the token is invalid,expired etc
     */
    public List<Role> allRoles(String token) {
        if (!TokenUtil.validateToken(token)) {
            throw new RuntimeException("token is not valid");
        }
        User user = TokenUtil.getUserByToken(token);
        TokenUtil.updateTokenTime(token);
        return user.getRoles();
    }
}
