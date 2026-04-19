package io.github.gabrielvelosoo.productservice.infrastructure.service;

import io.github.gabrielvelosoo.productservice.domain.service.ImageStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileSystemImageStorageService implements ImageStorageService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Value("${app.upload.url-prefix}")
    private String urlPrefix;

    @Override
    public String save(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID() + ".jpg";
        Path path = Paths.get(uploadDir, fileName);
        Files.createDirectories(path.getParent());
        BufferedImage originalImage = ImageIO.read(file.getInputStream());
        if(originalImage == null) {
            throw new IOException("Invalid image file");
        }
        BufferedImage jpgImage = new BufferedImage(
                originalImage.getWidth(),
                originalImage.getHeight(),
                BufferedImage.TYPE_INT_RGB
        );
        jpgImage.getGraphics().drawImage(originalImage, 0, 0, null);
        ImageIO.write(jpgImage, "jpg", path.toFile());
        return urlPrefix + fileName;
    }

    @Override
    public void delete(String imageUrl) throws IOException {
        if(imageUrl == null || imageUrl.isEmpty()) return;
        String fileName = imageUrl.replace(urlPrefix, "");
        Path path = Paths.get(uploadDir, fileName);
        Files.deleteIfExists(path);
    }

    @Override
    public String replace(String oldImageUrl, MultipartFile newFile) throws IOException {
        if(oldImageUrl != null && !oldImageUrl.isBlank()) {
            delete(oldImageUrl);
        }
        return save(newFile);
    }
}
