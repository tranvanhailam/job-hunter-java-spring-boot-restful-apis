package vn.kyler.job_hunter.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class RateLimitConfig {
    @Bean
    public Bucket bucket() {
        Refill refill = Refill.intervally(10, Duration.ofMinutes(1));
        Bandwidth limit = Bandwidth.classic(10, refill);
        Bucket bucket = Bucket.builder().addLimit(limit).build();
        return bucket;
    }
}
