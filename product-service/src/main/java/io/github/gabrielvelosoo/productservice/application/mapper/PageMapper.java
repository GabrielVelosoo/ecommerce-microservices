package io.github.gabrielvelosoo.productservice.application.mapper;

import io.github.gabrielvelosoo.productservice.application.dto.common.PageResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class PageMapper {

    private static final Logger logger = LogManager.getLogger(PageMapper.class);

    public <T, R> PageResponse<R> toPageResponse(Page<T> page, Function<T, R> mapper) {
        logger.debug("Mapping Page<T> to PageResponse<R>. pageNumber='{}', pageSize='{}', totalElements='{}'",
                page.getNumber(), page.getSize(), page.getTotalElements());
        logger.trace("Mapping individual elements using supplied mapper function...");
        var contentMapped = page.getContent()
                .stream()
                .map(item -> {
                    logger.trace("Mapping element: '{}'", item);
                    return mapper.apply(item);
                })
                .toList();
        logger.debug("Page mapped successfully. MappedSize='{}', totalPages='{}', hasNext='{}', hasPrevious='{}'",
                contentMapped.size(), page.getTotalPages(), page.hasNext(), page.hasPrevious());
        return new PageResponse<>(
                page.getContent().stream().map(mapper).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.hasNext(),
                page.hasPrevious()
        );
    }
}
