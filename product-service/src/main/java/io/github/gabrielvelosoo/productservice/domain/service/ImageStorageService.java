package io.github.gabrielvelosoo.productservice.domain.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageStorageService {

    String save(MultipartFile file) throws IOException;
    void delete(String imageUrl) throws IOException;
    String replace(String oldImageUrl, MultipartFile newFile) throws IOException;
}
