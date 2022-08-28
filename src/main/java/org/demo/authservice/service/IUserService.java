package org.demo.authservice.service;

import org.demo.authservice.entity.Role;
import org.demo.authservice.entity.User;

import java.util.List;

/**
 * @author Zhi Chen
 * @date 2022/8/27
 * @description
 */
public interface IUserService {

    boolean createUser(String userName, String password);

    boolean deleteUser(User user);

    boolean createRole(String roleName);

    boolean deleteRole(Role role);

    void addRoleToUser(User user, Role role);

    String authenticateUser(String userName, String password);

    void invalidateToken(String token);

    boolean checkRole(String token, Role role);

    List<Role> allRoles(String token);
}
