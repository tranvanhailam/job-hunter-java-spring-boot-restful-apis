package vn.kyler.job_hunter.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Meta {
    private int pageNumber;
    private int pageSize;
    private int totalPages;
    private long totalElements;    
}
