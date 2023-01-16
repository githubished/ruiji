package com.example.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.example.reggie.commmon.BaseContext;
import com.example.reggie.commmon.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//拦截所有请求
@WebFilter(filterName = "LoginCheckFilter",urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {

    //路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER=new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request=(HttpServletRequest) servletRequest;
        HttpServletResponse response=(HttpServletResponse) servletResponse;

        //获取本次请求的URI
        String requestURI=request.getRequestURI();
        log.info("成功拦截到请求：{}",requestURI);

        //定义不需要处理的请求路径
        String[] urls=new String[]{
          "/employee/login",
          "/employee/logout",
          "/backend/**",
          "/front/**",
          "/common/**",
          "/user/login",
          "/user/logout",
          "/user/sendEmail"
        };

        //判断本次请求是否放行
        boolean check =check(urls,requestURI);

        //如果为真不需要处理直接放行
        if(check){
            log.info("本次请求不需要处理:{}",requestURI);
            filterChain.doFilter(request,response);
            return;
        }

        //判断电脑端登录状态，如果已登录，就直接放行
        if(request.getSession().getAttribute("employee")!=null){
            Long empId=(Long)request.getSession().getAttribute("employee");
            log.info("用户{}已登录",empId);

            //将该员工id存入threadLocal中
            BaseContext.setCurrentId(empId);

            filterChain.doFilter(request,response);
            return;
        }

        //判断手机端状态，如果已登录，就直接放行
        if(request.getSession().getAttribute("user")!=null){
            Long userId=(Long)request.getSession().getAttribute("user");
            log.info("用户{}已登录",userId);

            //将该员工id存入threadLocal中
            BaseContext.setCurrentId(userId);

            filterChain.doFilter(request,response);
            return;
        }

        //如果未登录就返回未登录结果，通过输出流的方式向客户端页面响应数据
        log.info("用户未登录");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;


    }

    //路径匹配，看是否能放行
    public boolean check(String[] urls,String requestURI){
        for (String url : urls) {
            //看路径是否能匹配上
            boolean match=PATH_MATCHER.match(url,requestURI);
            if(match){
                return true;
            }
        }
        return false;
    }

}
