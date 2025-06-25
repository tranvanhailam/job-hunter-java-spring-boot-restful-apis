package vn.kyler.job_hunter.util;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import vn.kyler.job_hunter.service.exception.TooManyReqException;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {
    private final Bucket bucket;

    public RateLimitInterceptor(Bucket bucket) {
        this.bucket = bucket;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws TooManyReqException {
        ConsumptionProbe consumptionProbe = bucket.tryConsumeAndReturnRemaining(1);
        if (consumptionProbe.isConsumed()) {
            response.addHeader("X-Rate-Limit-Remaining", String.valueOf(consumptionProbe.getRemainingTokens()));
            return true; // Cho phép yêu cầu tiếp tục
        } else {
            long waitForRefillSeconds = consumptionProbe.getNanosToWaitForRefill();
            response.addHeader("X-Rate-Limit-Retry-After-Seconds", String.valueOf(waitForRefillSeconds));
            throw new TooManyReqException("You have exceeded the API request limit"); // res 429 Too Many Requests
//            response.sendError(HttpStatus.TOO_MANY_REQUESTS.value(), "You have exceeded the API request limit");
//            return false;
        }
    }
}
