package com.org.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.org.dao.MenuMapper;
import com.org.entity.Menu;
import com.org.service.MenuService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper,Menu> implements MenuService {

    @Autowired
    private MenuMapper menuMapper;

    /**
     * 根据用户获取所有菜单
     * @param id
     * @return
     */
    @Override
    public List<Menu> getUserMenu(String id) {
        return menuMapper.getUserMenu(id);
    }

    /**
     * 查找所有菜单集合
     * @param menuList
     * @return
     */
    @Override
    public JSONArray getMenuJsonByUser(List<Menu> menuList) {
        JSONArray jsonArr = new JSONArray();
        Collections.sort(menuList, (o1, o2) -> {
            if (o1.getOrderNum() == null || o2.getOrderNum() == null) {
                return -1;
            }
            if (o1.getOrderNum() > o2.getOrderNum()) {
                return 1;
            }
            if (o1.getOrderNum().equals(o2.getOrderNum())) {
                return 0;
            }
            return -1;
        });
        int pNum = 1000;
        for (Menu menu : menuList) {
            if (StringUtils.isEmpty(menu.getPId())) {
                Menu sysMenu = getChilds(menu, pNum, 0, menuList);
                jsonArr.add(sysMenu);
                pNum += 1000;
            }
        }
        return jsonArr;
    }


    /**
     * 封装菜单相关
     * @param menu
     * @param pNum
     * @param num
     * @param menuList
     * @return
     */
    public Menu getChilds(Menu menu, int pNum, int num, List<Menu> menuList) {
        for (Menu menus : menuList) {
            if (menu.getId().equals(menus.getPId()) && menus.getMenuType() == 0) {
                ++num;
                Menu m = getChilds(menus, pNum, num, menuList);
                m.setNum(pNum + num);
                menu.addChild(m);
            }
        }
        return menu;
    }

    /**
     * 获取元节点
     * @return
     */
    @Override
    public List<Menu> getMenuNotSuper() {
        return menuMapper.getMenuNotSuper();
    }

    /**
     * 获取子节点
     * @return
     */
    @Override
    public List<Menu> getMenuChildren(String id) {
        return menuMapper.getMenuChildren(id);
    }
}
