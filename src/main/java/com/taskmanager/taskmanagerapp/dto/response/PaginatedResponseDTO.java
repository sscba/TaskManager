package com.taskmanager.taskmanagerapp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaginatedResponseDTO<T> {
    private List<T> content;         // The actual data items
    private Integer currentPage;     // 0-indexed page number
    private Integer totalPages;      // Total number of pages
    private Long totalElements;      // Total number of records
    private Integer pageSize;        // How many records per page
    private Boolean isFirst;         // Is this the first page?
    private Boolean isLast;          // Is this the last page?
    private Boolean isEmpty;         // Does this page have zero records?

    // Static factory method: converts Spring's Page<T> into our DTO
    // This means you never write mapping code again in services
    public static <T> PaginatedResponseDTO<T> of(Page<T> page) {
        return PaginatedResponseDTO.<T>builder()
                .content(page.getContent())
                .currentPage(page.getNumber())
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .pageSize(page.getSize())
                .isFirst(page.isFirst())
                .isLast(page.isLast())
                .isEmpty(page.isEmpty())
                .build();
    }
}
