package com.yzm.security04.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yzm.security04.entity.Role;
import com.yzm.security04.mapper.RoleMapper;
import com.yzm.security04.service.RoleService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 角色表 服务实现类
 * </p>
 *
 * @author Yzm
 * @since 2021-10-19
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

}
