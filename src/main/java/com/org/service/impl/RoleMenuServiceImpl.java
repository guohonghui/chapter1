package com.org.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.org.dao.RoleMenuMapper;
import com.org.entity.RoleMenu;
import com.org.service.RoleMenuService;
import org.springframework.stereotype.Service;

@Service
public class RoleMenuServiceImpl extends ServiceImpl<RoleMenuMapper,RoleMenu> implements RoleMenuService {
}
