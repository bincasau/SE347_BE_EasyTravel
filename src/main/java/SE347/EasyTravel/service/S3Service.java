package SE347.EasyTravel.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class S3Service {

    private static final Logger logger = LoggerFactory.getLogger(S3Service.class);

    @Autowired
    private AmazonS3 s3Client;

    @Value("${aws.s3.bucketName}")
    private String bucketName;

    /**
     * Upload ảnh vào thư mục cụ thể với tên tùy chỉnh
     * @param file: File từ request
     * @param folderName: Tên thư mục (vd: "products", "users/avatars")
     * @param customFileName: Tên bạn muốn đặt (vd: "iphone-15")
     * @return URL của file sau khi upload
     */
    public String uploadImage(MultipartFile file, String folderName, String customFileName) throws IOException {

        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String fileName = folderName + "/" + customFileName + extension;

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        try {
            s3Client.putObject(new PutObjectRequest(bucketName, fileName, file.getInputStream(), metadata));
            return s3Client.getUrl(bucketName, fileName).toExternalForm();

        } catch (IOException e) {
            throw new IOException("Lỗi khi upload file lên S3: " + e.getMessage());
        }
    }
    /**
     * Xóa ảnh từ S3 bằng cách truyền folder và tên ảnh
     * Kiểm tra nếu là ảnh mặc định (default) thì bỏ qua không xóa
     * @param folderName: Tên thư mục (vd: "hotels")
     * @param fileNameWithExt: Tên ảnh bao gồm cả đuôi (vd: "default-avatar.jpg")
     */
    public void deleteImage(String folderName, String fileNameWithExt) {
        if (fileNameWithExt == null || fileNameWithExt.isEmpty()) {
            return;
        }

        if (fileNameWithExt.toLowerCase().contains("default")) {
            logger.info("Skipping deletion of default system image: {}", fileNameWithExt);
            return;
        }
        String fileKey = folderName + "/" + fileNameWithExt;
        try {
            s3Client.deleteObject(bucketName, fileKey);
            logger.info("Successfully deleted file: {}", fileKey);
        } catch (Exception e) {
            logger.error("Error deleting file from S3: {}", fileKey, e);
        }
    }
}
