package com.lihong.csuftcampus.interceptor;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lihong.csuftcampus.common.ErrorCode;
import com.lihong.csuftcampus.common.ResultUtil;
import com.lihong.csuftcampus.constant.RedisConstants;
import com.lihong.csuftcampus.model.dto.UserDTO;
import com.lihong.csuftcampus.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * token刷新拦截器
 */
@Slf4j
@Component
public class LoginWIthRefreshTokenInterceptor implements HandlerInterceptor {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. 从header中获取token
        String token = request.getHeader("authorization");
        log.info("token:{}", token);
        if (StrUtil.isBlank(token)) {
            response.setContentType("application/json");
            // 创建一个对象并将其转换为JSON格式
            String jsonData = new ObjectMapper().writeValueAsString(ResultUtil.error(ErrorCode.NOT_LOGIN));
            // 获取响应输出流并写入JSON内容
            response.getWriter().write(jsonData);
            return false;
        }

        // 2. 从redis中获取用户
        String tokenKey = RedisConstants.LOGIN_USER_KEY + token;
        Map<Object, Object> cacheUser = stringRedisTemplate.opsForHash().entries(tokenKey);

        // 3. 判断用户是否存在
        if (cacheUser.isEmpty()) {
            response.setContentType("application/json");
            // 创建一个对象并将其转换为JSON格式
            String jsonData = new ObjectMapper().writeValueAsString(ResultUtil.error(ErrorCode.NOT_LOGIN));
            // 获取响应输出流并写入JSON内容
            response.getWriter().write(jsonData);
            return false;
        }

        // 4. 将HashMap类型的User转换成UserDTO
        UserDTO userDTO = BeanUtil.fillBeanWithMap(cacheUser, new UserDTO(), false);

        // 5. 保存用户到ThreadLocal中
        UserHolder.saveUser(userDTO);

        // 6. 刷新token有效期
        stringRedisTemplate.expire(tokenKey, 3L, TimeUnit.HOURS);

        // 7. 放行

//        log.info("用户:{}发起请求", UserHolder.getUser().getUsername());
        return true;
    }

}

