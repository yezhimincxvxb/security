package com.yzm.security01.config;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * 自定义 安全用户模型
 * loadUserByUsername 的返回值是UserDetails，这是一个接口，
 * 可以使用它的子类org.springframework.security.core.userdetails.User
 * 也可以自定义一个，新增一些自定义的东西
 */
public class SecUserDetails extends User {

    private static final long serialVersionUID = 3033317408164827323L;

    // 自定义属性
    private List<Integer> roleIds;
    private Set<String> permissions;

    public SecUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        this(username, password, true, true, true, true, authorities);
    }

    public SecUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities, List<Integer> roleIds) {
        this(username, password, true, true, true, true, authorities);
        this.roleIds = roleIds;
    }

    public SecUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities, Set<String> permissions) {
        this(username, password, true, true, true, true, authorities);
        this.permissions = permissions;
    }

    public SecUserDetails(
            String username,
            String password,
            boolean enabled,
            boolean accountNonExpired,
            boolean credentialsNonExpired,
            boolean accountNonLocked,
            Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
    }

    public List<Integer> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(List<Integer> roleIds) {
        this.roleIds = roleIds;
    }

    public Set<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<String> permissions) {
        this.permissions = permissions;
    }
}
