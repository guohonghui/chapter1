package com.org.controller;

import com.alibaba.fastjson.JSONArray;
import com.org.entity.Menu;
import com.org.entity.User;
import com.org.filter.VerifyCodeUtils;
import com.org.service.MenuService;
import com.org.service.UserService;
import com.org.utils.ShiroUtils;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@Slf4j
public class LoginController {

    @Autowired
    private UserService userService;

    @Autowired
    private MenuService menuService;

    @GetMapping(value = "")
    public String loginInit() {
        return loginCheck();
    }

    @GetMapping(value = "goLogin")
    public String goLogin(Model model, ServletRequest request) {
        Subject sub = SecurityUtils.getSubject();
        if (sub.isAuthenticated()) {
            return "/main/main";
        } else {
            model.addAttribute("message", "请重新登录");
            return "/login";
        }
    }

    @GetMapping(value = "/login")
    public String loginCheck() {
        Subject sub = SecurityUtils.getSubject();
        Boolean flag2 = sub.isRemembered();
        boolean flag = sub.isAuthenticated() || flag2;
        Session session = sub.getSession();
        if (flag) {
            return "/main/main";
        }
        return "/login";
    }

    /**
     * 登录动作
     *
     * @param user
     * @param model
     * @param rememberMe
     * @return
     */
    @ApiOperation(value = "/login", httpMethod = "POST", notes = "登录method")
    @PostMapping(value = "/login")
    public String login(User user, Model model, String rememberMe, HttpServletRequest request) {
        String codeMsg = (String) request.getAttribute("shiroLoginFailure");
        if ("code.error".equals(codeMsg)) {
            model.addAttribute("message", "验证码错误");
            return "/login";
        }
        UsernamePasswordToken token = new UsernamePasswordToken(user.getUsername().trim(),
                user.getPassword());
        Subject subject = ShiroUtils.getSubject();
        String msg = null;
        try {
            subject.login(token);
            //subject.hasRole("admin");
            if (subject.isAuthenticated()) {
                return "redirect:/main";
            }
        } catch (UnknownAccountException e) {
            msg = "用户名/密码错误";
        } catch (IncorrectCredentialsException e) {
            msg = "用户名/密码错误";
        } catch (ExcessiveAttemptsException e) {
            msg = "登录失败多次，账户锁定10分钟";
        }
        if (msg != null) {
            model.addAttribute("message", msg);
        }
        return "/login";
    }

    @GetMapping("/main")
    public String main(){
        return "main/main";
    }

    @GetMapping(value = "/logout")
    public String logout() {
        Subject sub = SecurityUtils.getSubject();
        sub.logout();
        return "/login";
    }

    /**
     * 组装菜单json格式
     * update by 17/12/13
     *
     * @return
     */
    public JSONArray getMenuJson() {
        List<Menu> mList = menuService.getMenuNotSuper();
        JSONArray jsonArr = new JSONArray();
        for (Menu sysMenu : mList) {
            Menu menu = getChild(sysMenu.getId());
            jsonArr.add(menu);
        }
        return jsonArr;
    }

    public Menu getChild(String id) {
        Menu sysMenu = menuService.selectById(id);
        List<Menu> mList = menuService.getMenuChildren(id);
        for (Menu menu : mList) {
            Menu m = getChild(menu.getId());
            sysMenu.addChild(m);
        }
        return sysMenu;
    }


    @GetMapping(value = "/getCode")
    public void getYzm(HttpServletResponse response, HttpServletRequest request) {
        try {
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            response.setContentType("image/jpg");

            //生成随机字串
            String verifyCode = VerifyCodeUtils.generateVerifyCode(4);
            log.info("verifyCode:{}",verifyCode);
            //存入会话session
            HttpSession session = request.getSession(true);
            session.setAttribute("_code", verifyCode.toLowerCase());
            //生成图片
            int w = 146, h = 33;
            VerifyCodeUtils.outputImage(w, h, response.getOutputStream(), verifyCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
