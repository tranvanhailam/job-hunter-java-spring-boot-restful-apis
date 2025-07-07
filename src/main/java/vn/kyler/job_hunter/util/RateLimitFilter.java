package vn.kyler.job_hunter.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import vn.kyler.job_hunter.domain.response.RestResponse;

import java.io.IOException;
import java.util.function.Supplier;

@Component
public class RateLimitFilter extends OncePerRequestFilter {
    private final ObjectMapper objectMapper;
    private final Supplier<BucketConfiguration> bucketConfiguration;
    private final ProxyManager<String> proxyManager;

    public RateLimitFilter(ObjectMapper objectMapper, Supplier<BucketConfiguration> bucketConfiguration, ProxyManager<String> proxyManager) {
        this.objectMapper = objectMapper;
        this.bucketConfiguration = bucketConfiguration;
        this.proxyManager = proxyManager;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String currentUser = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";
        
        Bucket bucket = proxyManager.builder().build(currentUser, bucketConfiguration);
        ConsumptionProbe consumptionProbe = bucket.tryConsumeAndReturnRemaining(1);

        if (consumptionProbe.isConsumed()) {
            // Cho phép tiếp tục, gắn thêm header
            response.addHeader("X-Rate-Limit-Remaining", String.valueOf(consumptionProbe.getRemainingTokens()));
            filterChain.doFilter(request, response); // Cho đi tiếp
        } else {
            long waitForRefillSeconds = consumptionProbe.getNanosToWaitForRefill();
            response.addHeader("X-Rate-Limit-Retry-After-Seconds", String.valueOf(waitForRefillSeconds));

            RestResponse<Object> restResponse = new RestResponse<>();
            restResponse.setStatusCode(HttpStatus.TOO_MANY_REQUESTS.value());
            restResponse.setMessage("You have exceeded the API request limit");
            restResponse.setError("TooManyReqException");
            String restResponseJson = this.objectMapper.writeValueAsString(restResponse);

            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value()); // 429
            response.setContentType("application/json");
            response.getWriter().write(restResponseJson);
        }
    }
}