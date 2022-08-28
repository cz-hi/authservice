package org.demo.authservice.entity;

import java.util.Objects;

/**
 * @author Zhi Chen
 * @date 2022/8/27
 * @description
 */
public class Role {
    /**
     * roleName
     */
    private String roleName;

    public Role(String roleName) {
        Objects.requireNonNull(roleName, "roleName can't be null!");
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        Objects.requireNonNull(roleName, "roleName can't be null!");
        this.roleName = roleName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Role role = (Role) o;

        return roleName.equals(role.roleName);
    }

    @Override
    public int hashCode() {
        return roleName.hashCode();
    }
}
