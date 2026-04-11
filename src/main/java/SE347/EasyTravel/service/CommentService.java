package SE347.EasyTravel.service;

import SE347.EasyTravel.dao.BlogRepo;
import SE347.EasyTravel.dao.CommentRepo;
import SE347.EasyTravel.dao.UserRepo;
import SE347.EasyTravel.dto.CommentResponseDTO;
import SE347.EasyTravel.entity.Blog;
import SE347.EasyTravel.entity.Comment;
import SE347.EasyTravel.entity.User;
import SE347.EasyTravel.exception.ForbiddenException;
import SE347.EasyTravel.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    private final CommentRepo commentRepo;
    private final UserRepo userRepo;
    private final BlogRepo blogRepo;

    @Autowired
    public CommentService(CommentRepo commentRepo, UserRepo userRepo, BlogRepo blogRepo) {
        this.commentRepo = commentRepo;
        this.userRepo = userRepo;
        this.blogRepo = blogRepo;
    }

    @Transactional
    public Comment saveComment(String content, int blogId, String username) {
        User user = userRepo.findByUsername(username);
        if (user == null) throw new ResourceNotFoundException("Người dùng không tồn tại");
        Blog blog = blogRepo.findById(blogId)
                .orElseThrow(() -> new ResourceNotFoundException("Bài viết không tồn tại"));
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setUser(user);
        comment.setBlog(blog);

        return commentRepo.save(comment);
    }
    @Transactional
    public void deleteComment(int commentId, String username) {
        Comment comment = commentRepo.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Bình luận không tồn tại"));
        User currentUser = userRepo.findByUsername(username);
        if (currentUser == null) throw new ResourceNotFoundException("Người dùng không tồn tại");
        boolean isOwner = comment.getUser().getUsername().equals(username);
        boolean isAdmin = "ADMIN".equalsIgnoreCase(currentUser.getRole());
        if (!isOwner && !isAdmin) {
            throw new ForbiddenException("Bạn không có quyền xóa bình luận này!");
        }
        commentRepo.delete(comment);
    }

    @Transactional
    public Comment updateComment(int commentId, String newContent, String username) {
        Comment comment = commentRepo.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Bình luận không tồn tại"));

        User currentUser = userRepo.findByUsername(username);
        if (currentUser == null) throw new ResourceNotFoundException("Người dùng không tồn tại");
        boolean isOwner = comment.getUser().getUsername().equals(username);
        if (!isOwner) {
            throw new ForbiddenException("Bạn không có quyền chỉnh sửa bình luận này!");
        }

        comment.setContent(newContent);
        return commentRepo.save(comment);
    }
    public List<CommentResponseDTO> getCommentsByBlogId(int blogId) {
        // Kiểm tra xem blog có tồn tại không trước khi lấy comment (optional)
        if (!blogRepo.existsById(blogId)) {
            throw new ResourceNotFoundException("Bài viết không tồn tại");
        }

        // Gọi repo để lấy danh sách comment kèm User
        return commentRepo.findCommentsByBlogId(blogId);
    }

}
