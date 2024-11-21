package com.heima.behavior.gateway.filter;

import com.heima.behavior.gateway.util.AppJwtUtil;
import com.heima.model.wemedia.pojos.WmUser;
import com.heima.utils.thread.WmThreadLocalUtils;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class AuthorizationInterceptor implements HandlerInterceptor {

    // 在请求到达控制器之前进行 token 校验
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取请求头中的 token
        String token = request.getHeader("token");
        log.info(token);

        // 如果 token 为空，返回未授权
        if (StringUtils.isBlank(token)) {
            response.setStatus(401);
            response.getWriter().write("Unauthorized: Token is missing");
            return false;
        }

        Claims claimsBody;
        try {
            claimsBody = AppJwtUtil.getClaimsBody(token);

            // 验证 token 是否有效（例如是否过期）
            int result = AppJwtUtil.verifyToken(claimsBody);
            if (result == 1 || result == 2) {
                response.setStatus(401);
                response.getWriter().write("Unauthorized: Invalid token");
                return false;
            }

            // 获取用户 ID
            String userId = claimsBody.get("id").toString();

            // 直接使用 if 判断代替 Optional
            if (userId != null) {
                // 创建用户对象并存入 ThreadLocal
                WmUser wmUser = new WmUser();
                wmUser.setId(Integer.valueOf(userId));
                WmThreadLocalUtils.setUser(wmUser);
                log.info("User info set in ThreadLocal: " + wmUser);  // 确保输出有用信息
            }

        } catch (Exception e) {
            response.setStatus(401);
            response.getWriter().write("Unauthorized: Token parsing error");
            return false;
        }

        return true;  // 放行请求
    }

    // 确保线程结束后清理 ThreadLocal，避免内存泄漏
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        WmThreadLocalUtils.clear();  // 清理 ThreadLocal
    }
}
