package SE347.EasyTravel.dao;

import SE347.EasyTravel.entity.Blog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

@RepositoryRestResource(path="blogs")
public interface BlogRepo extends JpaRepository<Blog, Integer> {
    Page<Blog> findByTitleContainingIgnoreCase(
            @RequestParam("keyword") String keyword,
            Pageable pageable
    );
    Page<Blog> findByCreatedAtGreaterThanEqual(
            @RequestParam("startDate") Date startDate,
            Pageable pageable
    );
    Page<Blog> findByTagContainingIgnoreCase(
            @RequestParam("tag") String tag,
            Pageable pageable
    );
    @Query(value = """
        SELECT DISTINCT tag
        FROM blog
        WHERE tag IS NOT NULL AND tag <> ''
        ORDER BY tag
        """, nativeQuery = true)
    @RestResource(exported = false)
    List<String> findAllTags();
}
