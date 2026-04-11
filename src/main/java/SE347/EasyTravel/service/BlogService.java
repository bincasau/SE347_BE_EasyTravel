package SE347.EasyTravel.service;

import SE347.EasyTravel.dao.BlogRepo;
import SE347.EasyTravel.dao.UserRepo;
import SE347.EasyTravel.entity.Blog;
import SE347.EasyTravel.entity.User;
import SE347.EasyTravel.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class BlogService {

    private BlogRepo blogRepo;
    private UserRepo userRepo;
    private S3Service s3Service;

    @Autowired
    public BlogService(BlogRepo blogRepo, UserRepo userRepo, S3Service s3Service) {
        this.blogRepo = blogRepo;
        this.userRepo = userRepo;
        this.s3Service = s3Service;
    }

    @Transactional
    public Blog saveOrUpdateBlog(Blog blogDetails, MultipartFile thumbnailFile, String username) throws IOException {
        Blog blog;

        if (blogDetails.getBlogId() != 0) {
            blog = blogRepo.findById(blogDetails.getBlogId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy blog"));
        } else {
            blog = new Blog();
            User author = userRepo.findByUsername(username);
            blog.setUser(author);
        }

        blog.setTitle(blogDetails.getTitle());
        blog.setTag(blogDetails.getTag());
        blog.setDetails(blogDetails.getDetails());

        if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
            if (blog.getThumbnail() != null) {
                s3Service.deleteImage("blog", blog.getThumbnail());
            }
            String fileName = processUpload(thumbnailFile, "blog");
            blog.setThumbnail(fileName);
        }
        return blogRepo.save(blog);
    }

    @Transactional
    public void deleteBlog(int blogId) {
        Blog blog = blogRepo.findById(blogId)
                .orElseThrow(() -> new ResourceNotFoundException("Blog không tồn tại"));
        if (blog.getThumbnail() != null) {
            s3Service.deleteImage("blog", blog.getThumbnail());
        }
        blogRepo.delete(blog);
    }

    private String processUpload(MultipartFile file, String folder) throws IOException {
        String originalName = file.getOriginalFilename();
        String ext = (originalName != null && originalName.contains("."))
                ? originalName.substring(originalName.lastIndexOf(".")) : "";
        String customName = "blog_thumb_" + System.currentTimeMillis();
        s3Service.uploadImage(file, folder, customName);

        return customName + ext;
    }
}
