package com.yzm.security04.config;

import com.yzm.security04.entity.Permissions;
import com.yzm.security04.entity.Role;
import com.yzm.security04.service.PermissionsService;
import com.yzm.security04.service.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 细粒度的权限控制
 */
@Slf4j
@Component
public class SecPermissionEvaluator implements PermissionEvaluator {

    private final RoleService roleService;
    private final PermissionsService permissionsService;

    public SecPermissionEvaluator(RoleService roleService, PermissionsService permissionsService) {
        this.roleService = roleService;
        this.permissionsService = permissionsService;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Object targetUrl, Object targetPermission) {
        log.info("hasPermission 方法执行");
        // 获得loadUserByUsername()方法的结果
        SecUserDetails userDetails = (SecUserDetails) authentication.getPrincipal();
        // 获得loadUserByUsername()中注入的角色
        //Collection<GrantedAuthority> authorities = userDetails.getAuthorities();
        // 拿到SecUserDetails 自定义的roleIds值
        List<Integer> roleIds = userDetails.getRoleIds();

        // 查询角色对应的权限id信息
        List<Role> roles = roleService.listByIds(roleIds);
        Set<Integer> permIds = new HashSet<>();
        roles.forEach(role -> {
            Set<Integer> collect = Arrays.stream(role.getPIds().split(","))
                    .map(Integer::parseInt).collect(Collectors.toSet());
            permIds.addAll(collect);
        });

        List<Permissions> perms = permissionsService.listByIds(permIds);
        String permission = (String) targetUrl + ":" + (String) targetPermission;
        for (Permissions perm : perms) {
            // 如果访问的Url和权限用户符合的话，返回true
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
