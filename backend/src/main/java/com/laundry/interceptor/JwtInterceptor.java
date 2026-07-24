package com.laundry.interceptor;

import com.laundry.common.Result;
import com.laundry.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT 拦截器
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行 OPTIONS 请求
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        
        String token = request.getHeader("Authorization");
        
        if (token == null || token.isEmpty()) {
            writeError(response, "未登录或登录已过期");
            return false;
        }
        
        // 去掉 Bearer 前缀
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        try {
            Long userId = jwtUtil.getUserIdFromToken(token);
            if (userId == null) {
                writeError(response, "无效的token");
                return false;
            }
            // 将用户ID存入request
            request.setAttribute("userId", userId);
            request.setAttribute("role", jwtUtil.getRoleFromToken(token));
            return true;
        } catch (Exception e) {
            writeError(response, "token验证失败");
            return false;
        }
    }
    
    private void writeError(HttpServletResponse response, String message) throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(401);
        response.getWriter().write(objectMapper.writeValueAsString(Result.error(401, message)));
    }
}
