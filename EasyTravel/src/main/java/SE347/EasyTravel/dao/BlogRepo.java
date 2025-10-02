package SE347.EasyTravel.dao;

import SE347.EasyTravel.entity.Blog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path="blog")
public interface BlogRepo extends JpaRepository<Blog, Integer> {
}
