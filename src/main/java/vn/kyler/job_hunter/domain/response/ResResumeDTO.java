package vn.kyler.job_hunter.domain.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;
import vn.kyler.job_hunter.domain.Job;
import vn.kyler.job_hunter.domain.User;
import vn.kyler.job_hunter.util.constant.StatusEnum;

import java.time.Instant;

@Getter
@Setter
public class ResResumeDTO {
    private long id;
    private String email;
    private String url;
    @Enumerated(EnumType.STRING)
    private StatusEnum status;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss a", timezone = "GMT+7")
    private Instant createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss a", timezone = "GMT+7")
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;

    private User user;
    private Job job;

    @Getter
    @Setter
    public static class User {
        private long id;
        private String name;
    }

    @Getter
    @Setter
    public static class Job {
        private long id;
        private String name;
    }
}
