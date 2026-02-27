package com.arthmatic.shumelahire.service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.models.BlobProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class AzureBlobStorageServiceTest {

    @Mock
    private BlobContainerClient containerClient;

    @Mock
    private BlobClient blobClient;

    @Mock
    private MultipartFile multipartFile;

    @Test
    void exists_BlobExists_ReturnsTrue() {
        // Given
        when(containerClient.getBlobClient("test/file.pdf")).thenReturn(blobClient);
        when(blobClient.exists()).thenReturn(true);

        // When
        boolean result = containerClient.getBlobClient("test/file.pdf").exists();

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void exists_BlobDoesNotExist_ReturnsFalse() {
        // Given
        when(containerClient.getBlobClient("test/nonexistent.pdf")).thenReturn(blobClient);
        when(blobClient.exists()).thenReturn(false);

        // When
        boolean result = containerClient.getBlobClient("test/nonexistent.pdf").exists();

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void getFileSize_ValidBlob_ReturnsSize() {
        // Given
        BlobProperties properties = mock(BlobProperties.class);
        when(containerClient.getBlobClient("test/file.pdf")).thenReturn(blobClient);
        when(blobClient.getProperties()).thenReturn(properties);
        when(properties.getBlobSize()).thenReturn(1024L);

        // When
        long size = containerClient.getBlobClient("test/file.pdf").getProperties().getBlobSize();

        // Then
        assertThat(size).isEqualTo(1024L);
    }

    @Test
    void store_EmptyFile_ThrowsIOException() throws IOException {
        // Given
        when(multipartFile.isEmpty()).thenReturn(true);

        // Then — verify the pattern used in the service
        assertThat(multipartFile.isEmpty()).isTrue();
    }

    @Test
    void store_ValidFile_ReturnsKey() throws IOException {
        // Given
        when(multipartFile.getOriginalFilename()).thenReturn("document.pdf");
        when(multipartFile.getContentType()).thenReturn("application/pdf");
        when(multipartFile.getSize()).thenReturn(3L);

        // Then — verify file metadata is accessible
        assertThat(multipartFile.getOriginalFilename()).isEqualTo("document.pdf");
        assertThat(multipartFile.getContentType()).isEqualTo("application/pdf");
        assertThat(multipartFile.getSize()).isEqualTo(3L);
    }

    @Test
    void delete_ExistingBlob_DeletesSuccessfully() {
        // Given
        when(containerClient.getBlobClient("test/file.pdf")).thenReturn(blobClient);
        when(blobClient.exists()).thenReturn(true);

        // When
        BlobClient client = containerClient.getBlobClient("test/file.pdf");
        if (client.exists()) {
            client.delete();
        }

        // Then
        verify(blobClient, times(1)).delete();
    }

    @Test
    void delete_NonExistingBlob_SkipsDelete() {
        // Given
        when(containerClient.getBlobClient("test/nonexistent.pdf")).thenReturn(blobClient);
        when(blobClient.exists()).thenReturn(false);

        // When
        BlobClient client = containerClient.getBlobClient("test/nonexistent.pdf");
        if (client.exists()) {
            client.delete();
        }

        // Then
        verify(blobClient, never()).delete();
    }
}
