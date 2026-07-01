package com.library.frontms.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class JwtSessionTokenProvider {

    public String getToken() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return null;
        }
        HttpServletRequest request = attributes.getRequest();
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        return (String) session.getAttribute(JwtAuthenticationSuccessHandler.SESSION_ATTR_TOKEN);
    }
}
