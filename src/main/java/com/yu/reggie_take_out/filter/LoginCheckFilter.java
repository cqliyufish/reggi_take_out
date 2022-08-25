package com.yu.reggie_take_out.filter;

import com.alibaba.fastjson.JSON;
import com.yu.reggie_take_out.common.BaseContext;
import com.yu.reggie_take_out.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 检查是否已经登录
 * "/*" 拦截所有请求
 * @ServletComponentScan 加载application.java中，这样才扫描@WebFilter
 */
@Slf4j
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
    // 路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        //1. 获得本次请求URI
        String requestURI = request.getRequestURI();
        log.info("拦截到请：{}", requestURI);
        //2. 判断本次请求是否需要处理
        // 不需要处理url "**"backend下面所有的子文件，多级目录，一个*只能1级子目录
        String[] urls = new String[] {
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",
                "/user/login"
        };
        boolean check = check(urls, requestURI);

        // 3. 不需要处理，放行
        if(check){
            //放行
            log.info("{} 不需要处理",requestURI);
            filterChain.doFilter(request,response);
            return;
        }

        // 4-1. 判断员工用户是否已经登录
        if (request.getSession().getAttribute("employee") != null){
            Long empId = (Long) request.getSession().getAttribute("employee");
            //设置LocalThread，方便MyMetaObjectHandler中自动填充公共字段用
            BaseContext.setCurrentId(empId);
            filterChain.doFilter(request,response);
            return;
        }

        // 4-2. 判断移动用户是否已经登录
        if (request.getSession().getAttribute("user") != null){
            Long userId = (Long) request.getSession().getAttribute("user");
            //设置LocalThread，方便MyMetaObjectHandler中自动填充公共字段用
            BaseContext.setCurrentId(userId);
            filterChain.doFilter(request,response);
            return;
        }

        // 5. 未登录，通过输出流，向客户端输出
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;

    }

    /***
     *
     * @param requestURI
     * @return
     */
    public boolean check(String[] urls, String requestURI) {
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if(match){
                return true;
            }
        }
        return false;
    }
}
