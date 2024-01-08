package dblab.sharing_platform.service.file;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import dblab.sharing_platform.exception.file.FileUploadFailureException;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
public class FileService {

    @Value("${cloud.aws.s3.bucket}")
    private String BUCKET;

    private final AmazonS3Client amazonS3Client;

    public void upload(MultipartFile file, String filename, String folder) {
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());
            amazonS3Client.putObject(BUCKET + folder, filename, file.getInputStream(), metadata);
        } catch (IOException e) {
            throw new FileUploadFailureException();
        }
    }

    public void delete(String fileName, String folder) {
        DeleteObjectRequest request = new DeleteObjectRequest(BUCKET + folder, fileName);
        amazonS3Client.deleteObject(request);
    }
}
