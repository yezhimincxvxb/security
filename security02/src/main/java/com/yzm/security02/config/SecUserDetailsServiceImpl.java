package com.yzm.security02.config;

import com.yzm.security02.entity.Role;
import com.yzm.security02.entity.User;
import com.yzm.security02.service.RoleService;
import com.yzm.security02.service.UserService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 自定义UserDetailsService，重写loadUserByUsername方法，查询数据库获取用户信息和权限信息
 * Spring Security进行用户认证时，需要根据用户的账号、密码、权限等信息进行认证，
 * 因此，需要根据查询到的用户信息封装成一个认证用户对象并交给Spring Security进行认证。
 * 查询用户信息并封装成认证用户对象的过程是在UserDetailsService接口的实现类（需要用户自己实现）中完成的
 */
@Service
public class SecUserDetailsServiceImpl implements UserDetailsService {

    private final UserService userService;
    private final RoleService roleService;

    public SecUserDetailsServiceImpl(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.lambdaQuery().eq(User::getUsername, username).one();
        if (user == null) {
            throw new UsernameNotFoundException(String.format("用户'%s'不存在", username));
        }

        List<Integer> roleIds = Arrays.stream(user.getRIds().split(","))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
        List<Role> roleList = roleService.listByIds(roleIds);
        List<SimpleGrantedAuthority> authorities = roleList.stream()
                .map(Role::getRName)
                .distinct()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        // 这里我们的权限只是基于角色的
        return new SecUserDetails(username, user.getPassword(), authorities, roleIds);
    }
}