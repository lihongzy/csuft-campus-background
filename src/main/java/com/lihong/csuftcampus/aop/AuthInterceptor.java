package com.lihong.csuftcampus.aop;

import com.lihong.csuftcampus.annotation.AuthCheck;
import com.lihong.csuftcampus.common.ErrorCode;
import com.lihong.csuftcampus.exception.BusinessException;
import com.lihong.csuftcampus.model.dto.UserDTO;
import com.lihong.csuftcampus.utils.UserHolder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * AOP切面类对拦截的方法进行权限验证
 */
@Aspect
@Component
public class AuthInterceptor {


    /**
     * 切面拦截权限验证
     *
     * @param joinPoint 连接点
     * @param authCheck 权限
     */
    @Around("@annotation(authCheck)")
    public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
        // 1. 获取权限
        int[] anyRole = authCheck.anyRole();
        int mustRole = authCheck.mustRole();

        // 2. 获取当前线程中的请求的属性对象。
        UserDTO user = UserHolder.getUser();

        // 3. 拥有任意权限即通过
        if (anyRole.length > 0) {
            int userRole = user.getUserRole();
            if (Arrays.stream(anyRole).noneMatch(role -> role == userRole)) {
                throw new BusinessException(ErrorCode.NO_AUTH);
            }
        }

        // 4. 必须有所有权限才通过
        if (mustRole != user.getUserRole()) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }

        // 通过权限校验，放行
        return joinPoint.proceed();
    }

}
