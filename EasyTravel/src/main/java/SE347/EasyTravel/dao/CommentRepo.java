package SE347.EasyTravel.dao;

import SE347.EasyTravel.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "comment")
public interface CommentRepo extends JpaRepository<Comment, Integer> {
}
