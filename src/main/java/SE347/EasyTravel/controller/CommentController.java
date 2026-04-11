package SE347.EasyTravel.controller;

import SE347.EasyTravel.dao.CommentRepo;
import SE347.EasyTravel.dto.CommentResponseDTO;
import SE347.EasyTravel.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CommentController {
    @Autowired
    private CommentService commentService;

    @GetMapping("/blogs/{blogId}/comments")
    public List<CommentResponseDTO> getCommentsByBlog(@PathVariable int blogId) {
        return commentService.getCommentsByBlogId(blogId);
    }
}
