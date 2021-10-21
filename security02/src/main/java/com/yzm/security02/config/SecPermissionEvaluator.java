package com.yzm.security02.config;

import com.yzm.security02.entity.Permissions;
import com.yzm.security02.service.PermissionsService;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;

/**
 * 细粒度的权限控制
 */
@Component
public class SecPermissionEvaluator implements PermissionEvaluator {

    private final PermissionsService permissionsService;

    public SecPermissionEvaluator(PermissionsService permissionsService) {
        this.permissionsService = permissionsService;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Object targetUrl, Object targetPermission) {
        // 获得loadUserByUsername()方法的结果
        SecUserDetails userDetails = (SecUserDetails) authentication.getPrincipal();
        // 获得loadUserByUsername()中注入的角色
        //Collection<GrantedAuthority> authorities = userDetails.getAuthorities();
        // 拿到SecUserDetails 自定义的roleIds值
        List<Integer> roleIds = userDetails.getRoleIds();

        // 查询角色对应的所有权限信息
        List<Permissions> perms = permissionsService.listByIds(roleIds);

        for (Permissions perm : perms) {
            // 如果访问的Url和权限用户符合的话，返回true
            String permission = (String) targetUrl + ":" + (String) targetPermission;
            if (permission.equals(perm.getPName())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        return false;
    }

}
