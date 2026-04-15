package io.github.gabrielvelosoo.productservice.integration.infrastructure.storage;

import io.github.gabrielvelosoo.productservice.domain.service.ImageStorageService;
import io.github.gabrielvelosoo.productservice.infrastructure.storage.FileSystemImageStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileSystemImageStorageServiceIT {

    ImageStorageService imageStorageService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        FileSystemImageStorageService fileSystemImageStorageService = new FileSystemImageStorageService();
        ReflectionTestUtils.setField(fileSystemImageStorageService, "uploadDir", tempDir.toString());
        ReflectionTestUtils.setField(fileSystemImageStorageService, "urlPrefix", "http://localhost/images/");
        imageStorageService = fileSystemImageStorageService;
    }

    @Nested
    class SaveTests {

        @Test
        void shouldSaveImageSuccessfully() throws IOException {
            MockMultipartFile file = createValidImage();
            String imageUrl = imageStorageService.save(file);
            assertNotNull(imageUrl);
            assertTrue(imageUrl.startsWith("http://localhost/images/"));
            String fileName = imageUrl.replace("http://localhost/images/", "");
            Path savedFile = tempDir.resolve(fileName);
            assertTrue(Files.exists(savedFile));
            assertTrue(Files.size(savedFile) > 0);
        }

        @Test
        void shouldThrowExceptionWhenFileIsNotAnImage() {
            MockMultipartFile invalidFile = new MockMultipartFile(
                    "file",
                    "file.txt",
                    "text/plain",
                    "invalid content".getBytes()
            );
            assertThrows(
                    IOException.class,
                    () -> imageStorageService.save(invalidFile)
            );
        }
    }

    @Nested
    class DeleteTests {

        @Test
        void shouldDeleteImageSuccessfully() throws IOException {
            MockMultipartFile file = createValidImage();
            String imageUrl = imageStorageService.save(file);
            String fileName = imageUrl.replace("http://localhost/images/", "");
            Path savedFile = tempDir.resolve(fileName);
            assertTrue(Files.exists(savedFile));
            imageStorageService.delete(imageUrl);
            assertFalse(Files.exists(savedFile));
        }

        @Test
        void shouldIgnoreDeleteWhenImageUrlIsNullOrEmpty() {
            assertDoesNotThrow(() -> imageStorageService.delete(null));
            assertDoesNotThrow(() -> imageStorageService.delete(""));
        }
    }

    @Nested
    class ReplaceTests {

        @Test
        void shouldReplaceImageSuccessfully() throws IOException {
            MockMultipartFile oldFile = createValidImage();
            String oldImageUrl = imageStorageService.save(oldFile);
            MockMultipartFile newFile = createValidImage();
            String newImageUrl = imageStorageService.replace(oldImageUrl, newFile);
            String oldFileName = oldImageUrl.replace("http://localhost/images/", "");
            String newFileName = newImageUrl.replace("http://localhost/images/", "");
            assertFalse(Files.exists(tempDir.resolve(oldFileName)));
            assertTrue(Files.exists(tempDir.resolve(newFileName)));
        }

        @Test
        void shouldSaveImageWhenOldImageIsNull() throws IOException {
            MockMultipartFile newFile = createValidImage();
            String newImageUrl = imageStorageService.replace(null, newFile);
            String fileName = newImageUrl.replace("http://localhost/images/", "");
            assertTrue(Files.exists(tempDir.resolve(fileName)));
        }
    }

    private MockMultipartFile createValidImage() throws IOException {
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", output);
        return new MockMultipartFile(
                "file",
                "image.jpg",
                "image/jpeg",
                output.toByteArray()
        );
    }
}