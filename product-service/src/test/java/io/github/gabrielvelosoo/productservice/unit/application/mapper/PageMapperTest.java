package io.github.gabrielvelosoo.productservice.unit.application.mapper;

import io.github.gabrielvelosoo.productservice.application.dto.common.PageResponse;
import io.github.gabrielvelosoo.productservice.application.mapper.PageMapper;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PageMapperTest {

    PageMapper pageMapper = new PageMapper();

    @Test
    void shouldMapPageToPageResponseCorrectly() {
        Page<String> page = new PageImpl<>(
                List.of("Notebook", "Mouse"),
                PageRequest.of(0, 2),
                5
        );
        PageResponse<Integer> response = pageMapper.toPageResponse(page, String::length);
        assertEquals(List.of(8, 5), response.content());
        assertEquals(0, response.page());
        assertEquals(2, response.size());
        assertEquals(5, response.totalElements());
        assertEquals(3, response.totalPages());
        assertTrue(response.hasNext());
        assertFalse(response.hasPrevious());
    }
}