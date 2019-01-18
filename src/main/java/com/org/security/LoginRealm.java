package com.org.security;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.org.base.CurrentMenu;
import com.org.base.CurrentRole;
import com.org.base.CurrentUser;
import com.org.entity.Menu;
import com.org.entity.Role;
import com.org.entity.User;
import com.org.service.MenuService;
import com.org.service.UserService;
import com.org.utils.ShiroUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
public class LoginRealm extends AuthorizingRealm {

    @Autowired
    private UserService userService;

    @Autowired
    private MenuService menuService;

    /**
     * 获取认证
     * @param principalCollection
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        String name= (String) principalCollection.getPrimaryPrincipal();
        //根据用户获取角色 根据角色获取所有按钮权限
        CurrentUser cUser= (CurrentUser) ShiroUtils.getSession().getAttribute("curentUser");
        for(CurrentRole cRole:cUser.getCurrentRoleList()){
           info.addRole(cRole.getId());
        }
        for(CurrentMenu cMenu:cUser.getCurrentMenuList()){
          if(!StringUtils.isEmpty(cMenu.getPermission()))
          info.addStringPermission(cMenu.getPermission());
        }
        return info;
    }

    /**
     * 获取授权
     * @param authenticationToken
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        UsernamePasswordToken upToken = (UsernamePasswordToken) authenticationToken;
        String name=upToken.getUsername();
        String username=(String)authenticationToken.getPrincipal();
        EntityWrapper<User> wrapper = new EntityWrapper<>();
        wrapper.eq("username",username);
        User s=null;
        try {
          s = userService.selectOne(wrapper);
        }catch (Exception e){
          e.printStackTrace();
        }
        if(s==null){
          throw new UnknownAccountException("账户密码不正确");
        }else{
          CurrentUser currentUser=new CurrentUser(s.getId(),s.getUsername(),s.getAge(),s.getEmail(),s.getPhoto(),s.getRealName());
          Subject subject = ShiroUtils.getSubject();
          /**角色权限封装进去*/
          //根据用户获取菜单
          List<Menu> menuList=new ArrayList<>(new HashSet<>(menuService.getUserMenu(s.getId())));
          JSONArray json = menuService.getMenuJsonByUser(menuList);
          Session session= subject.getSession();
          session.setAttribute("menu",json);
          CurrentMenu currentMenu=null;
          List<CurrentMenu> currentMenuList=new ArrayList<>();
          List<Role> roleList=new ArrayList<>();
          for(Menu m:menuList){
           currentMenu=new CurrentMenu(m.getId(),m.getName(),m.getPId(),m.getUrl(),m.getOrderNum(),m.getIcon(),m.getPermission(),m.getMenuType(),m.getNum());
            currentMenuList.add(currentMenu);
              roleList.addAll(m.getRoleList());
          }
          roleList= new ArrayList<>(new HashSet<>(roleList));
          List<CurrentRole> currentRoleList=new ArrayList<>();
          CurrentRole role=null;
          for(Role r:roleList){
            role=new CurrentRole(r.getId(),r.getRoleName(),r.getRemark());
            currentRoleList.add(role);
          }
          currentUser.setCurrentRoleList(currentRoleList);
          currentUser.setCurrentMenuList(currentMenuList);
          session.setAttribute("curentUser",currentUser);
        }
        ByteSource byteSource=ByteSource.Util.bytes(username);
        return new SimpleAuthenticationInfo(username,s.getPassword(), byteSource, getName());
    }
}
