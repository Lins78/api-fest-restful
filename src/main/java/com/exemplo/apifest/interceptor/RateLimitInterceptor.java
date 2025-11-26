package com.exemplo.apifest.interceptor;

import com.exemplo.apifest.config.RateLimitingConfig;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Interceptor para Rate Limiting
 * Aplica controle de taxa baseado no IP do cliente
 */
@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    @Autowired
    private RateLimitingConfig rateLimitingConfig;

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        String ipAddress = getClientIpAddress(request);
        String requestURI = request.getRequestURI();

        Bucket bucket;
        
        // Aplicar diferentes limites baseados no endpoint
        if (requestURI.startsWith("/api/auth")) {
            bucket = rateLimitingConfig.resolveAuthBucket(ipAddress);
        } else if (requestURI.startsWith("/api/admin")) {
            bucket = rateLimitingConfig.resolveAdminBucket(ipAddress);
        } else {
            bucket = rateLimitingConfig.resolveBucket(ipAddress);
        }

        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
        
        if (probe.isConsumed()) {
            // Adicionar headers informativos sobre rate limiting
            response.addHeader("X-Rate-Limit-Remaining", String.valueOf(probe.getRemainingTokens()));
            response.addHeader("X-Rate-Limit-Retry-After-Seconds", 
                String.valueOf(probe.getNanosToWaitForRefill() / 1_000_000_000));
            return true;
        } else {
            // Rate limit excedido
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.addHeader("X-Rate-Limit-Retry-After-Seconds", 
                String.valueOf(probe.getNanosToWaitForRefill() / 1_000_000_000));
            response.getWriter().write("Too many requests - Rate limit exceeded");
            return false;
        }
    }

    /**
     * Extrai o endereço IP real do cliente considerando proxies e load balancers
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}