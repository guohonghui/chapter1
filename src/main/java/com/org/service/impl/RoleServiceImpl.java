package com.org.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.org.dao.RoleMapper;
import com.org.entity.Role;
import com.org.service.RoleService;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper,Role> implements RoleService {

}
