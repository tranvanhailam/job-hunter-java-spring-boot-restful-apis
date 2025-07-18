package vn.kyler.job_hunter.domain.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResultPaginationDTO {
    private Meta meta;
    private Object result;

    @Getter
    @Setter
    public static class Meta {
        private int pageNumber;
        private int pageSize;
        private int totalPages;
        private long totalElements;
        
    }
}
