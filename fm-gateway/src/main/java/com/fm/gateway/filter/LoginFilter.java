package com.fm.gateway.filter;

import com.fm.auth.utils.JwtUtils;
import com.fm.common.utils.CookieUtils;
import com.fm.gateway.properties.FilterProperties;
import com.fm.gateway.properties.JwtProperties;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;


@Slf4j
@Component
@EnableConfigurationProperties({JwtProperties.class, FilterProperties.class})
public class LoginFilter extends ZuulFilter {

    @Autowired
    private JwtProperties props;

    @Autowired
    private FilterProperties filterProps;

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return FilterConstants.PRE_DECORATION_FILTER_ORDER - 1;
    }

    @Override
    public boolean shouldFilter() {
        //获取上下文
        RequestContext ctx = RequestContext.getCurrentContext();
        //request
        HttpServletRequest request = ctx.getRequest();
        //获取请求的url路径
        String requestURI = request.getRequestURI();
        //判断白名单
        return !isAllowPath(requestURI);
    }

    @Override
    public Object run() {
        //获取上下文
        RequestContext context = RequestContext.getCurrentContext();
        //获取request
        HttpServletRequest request = context.getRequest();
        //获取cookie中的token
        String token = CookieUtils.getCookieValue(request, props.getCookieName());
        try {
            //从Token获取解析用户信息
            JwtUtils.getUserInfo(props.getPublicKey(), token);
            //解析成功，什么都不做，放行

            //todo 如果做权限管理的话，在这做权限检验
            //通过获取用户信息中的权限信息，对可访问的路径进行比对，实现权限控制
        } catch (Exception e) {
            //解析token失败，未登录，拦截，返回403
            context.setSendZuulResponse(false);
            context.setResponseStatusCode(403);
            log.error("非法访问，未登录，地址：{}", request.getRemoteHost(), e);
        }
        return null;
    }

    /**
     * 判断请求URI是不是白名单中的URI
     *
     * @param requestURI
     * @return
     */
    private Boolean isAllowPath(String requestURI) {
        boolean flag = false;

        for (String allowPath : filterProps.getAllowPaths()) {
            if (requestURI.startsWith(allowPath)) {
                //允许
                flag = true;
                break;
            }
        }
        return flag;


    }
}
