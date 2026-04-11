package SE347.EasyTravel.dao;

import SE347.EasyTravel.dto.CommentResponseDTO;
import SE347.EasyTravel.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(path = "comments")
public interface CommentRepo extends JpaRepository<Comment, Integer> {
    @Query("SELECT new SE347.EasyTravel.dto.CommentResponseDTO(c.commentId, c.content, c.createdAt, u.name, u.avatar) " +
            "FROM Comment c JOIN c.user u " +
            "WHERE c.blog.blogId = :blogId")
    List<CommentResponseDTO> findCommentsByBlogId(@Param("blogId") int blogId);
}
