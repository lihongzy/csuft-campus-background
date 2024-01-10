package com.lihong.csuftcampus.aop;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import com.lihong.csuftcampus.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;


@Aspect
@Component
@Slf4j
public class LogInterceptor {
    /**
     * 执行拦截
     */
    @Around("execution(* com.lihong.csuftcampus.controller.*.*(..))")
    public Object doInterceptor(ProceedingJoinPoint point) throws Throwable {
        // 计时
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        // 获取请求路径
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest httpServletRequest = ((ServletRequestAttributes) requestAttributes).getRequest();

        String requestId = UUID.randomUUID().toString(true);
        String username = "未登录用户";
        if (UserHolder.getUser() != null) {
            username = UserHolder.getUser().getUsername();
        }
        String url = httpServletRequest.getRequestURI();
        String ip = httpServletRequest.getRemoteHost();
        // 获取请求参数
        Object[] args = point.getArgs();
        String reqParam = "[" + StrUtil.join(", ", args) + "]";

        // 输出请求日志
        log.info("请求开始, 请求id: {}, 用户:{}, 请求路径: {}, ip地址: {}, 请求参数: {}", requestId, username, url, ip, reqParam);
        // 执行原方法
        Object result = point.proceed();
        // 输出响应日志
        stopWatch.stop();
        long totalTimeMillis = stopWatch.getTotalTimeMillis();
        log.info("请求结束, 请求id: {}, 花费时间: {}ms", requestId, totalTimeMillis);
        return result;
    }
}
