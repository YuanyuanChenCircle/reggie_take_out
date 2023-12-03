package com.itheima.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 检查用户是否完成登录
 */
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // 获取本次请求的URI
        String requestURI = request.getRequestURI();
        log.info("拦截到请求：{}", requestURI);
        // 定义不需要处理的路径
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",// 移动端发送短信
                "/user/login"// 移动端登录
        };

        boolean isFilter = check(urls, requestURI);
        if (isFilter) {
            log.info("本次请求{}不需要处理", requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        if (request.getSession().getAttribute("employee") != null) {
            log.info("本次请求{}不需要处理", requestURI);
            Long empId = (Long) request.getSession().getAttribute("employee");
            if (empId != null) {
                BaseContext.setCurrentId(empId);
            }

            filterChain.doFilter(request, response);
            return;
        }

        // 移动端用户的登录状态
        if (request.getSession().getAttribute("user") != null) {
            log.info("本次请求{}不需要处理", requestURI);
            Long userId = (Long) request.getSession().getAttribute("user");
            if (userId != null) {
                BaseContext.setCurrentId(userId);
            }

            filterChain.doFilter(request, response);
            return;
        }

        log.info("用户未登录{}", requestURI);
        // 如果未登录，
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
    }

    /**
     * 路径匹配，检查本次请求是否需要放行
     *
     * @param requestURI requestURI
     * @return boolean
     */
    public boolean check(String[] urls, String requestURI) {
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if (match) {
                return true;
            }
        }

        return false;
    }
}
