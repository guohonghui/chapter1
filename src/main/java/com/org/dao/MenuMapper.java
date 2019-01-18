package com.org.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.org.entity.Menu;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MenuMapper extends BaseMapper<Menu> {

    /**
     * 根据用户获取所有菜单
     * @param id
     * @return
     */
    List<Menu> getUserMenu(@Param("id") String id);

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