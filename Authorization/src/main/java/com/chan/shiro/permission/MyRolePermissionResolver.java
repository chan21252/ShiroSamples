package com.chan.shiro.permission;

import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.permission.RolePermissionResolver;
import org.apache.shiro.authz.permission.WildcardPermission;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * MyRolePermissionResolver
 *
 * @author Chan
 * @since 2021/1/12
 */
public class MyRolePermissionResolver implements RolePermissionResolver {
    @Override
    public Collection<Permission> resolvePermissionsInRole(String roleString) {
        if("admin".equals(roleString)) {
            return Collections.singletonList((Permission) new WildcardPermission("order:*"));
        }
        return null;
    }
}