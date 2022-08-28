package org.demo.authservice.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Zhi Chen
 * @date 2022/8/27
 * @description
 */
public class User {
    /**
     * userName:unique identifier of one user
     */
    private String userName;

    /**
     * password
     */
    private String password;
    /**
     * roles of the user
     */
    private List<Role> roles;

    public User(String userName, String password) {
        Objects.requireNonNull(userName);
        Objects.requireNonNull(password);
        this.userName = userName;
        this.password = password;
        roles = new ArrayList<>();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        Objects.requireNonNull(userName);
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        Objects.requireNonNull(password);
        this.password = password;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public boolean hasRole(Role role) {
        return this.roles.contains(role);
    }
}
