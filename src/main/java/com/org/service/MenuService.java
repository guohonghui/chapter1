package com.org.service;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.service.IService;
import com.org.entity.Menu;

import java.util.List;

public interface MenuService extends IService<Menu> {

    /**
     * 根据用户获取所有菜单
     * @param id
     * @return
     */
    List<Menu> getUserMenu(String id);

    /**
     * 查找所有菜单集合
     * @param menuList
     * @return
     */
    JSONArray getMenuJsonByUser(List<Menu> menuList);

    /**
     * 获取元节点
     * @return
     */
    List<Menu> getMenuNotSuper();

    /**
     * 获取子节点
     * @return
     */
    List<Menu> getMenuChildren(String id);
}
