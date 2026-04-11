package SE347.EasyTravel.dao;

import SE347.EasyTravel.dto.HotelCard;
import SE347.EasyTravel.entity.Hotel;
import SE347.EasyTravel.entity.Tour;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource(path = "hotels")
public interface HotelRepo extends JpaRepository<Hotel, Integer> {
    Page<Hotel> findByNameContainingIgnoreCaseOrAddressContainingIgnoreCase(
            String nameKeyword,
            String addressKeyword,
            Pageable pageable
    );
    @Query(value = """
    SELECT * FROM hotel h
    WHERE TRIM(SUBSTRING_INDEX(h.address, ',', -1)) = :province
    """, nativeQuery = true)
    Page<Hotel> findByProvince(
            @Param("province") String province,
            Pageable pageable
    );
    @Query(value = """
        SELECT DISTINCT TRIM(SUBSTRING_INDEX(h.address, ',', -1)) AS province
        FROM hotel h
        WHERE h.address IS NOT NULL
          AND h.address <> ''
        ORDER BY province
        """,
            nativeQuery = true)
    @RestResource(exported = false)
    List<String> findAllProvinces();

    Optional<Hotel> findByManager_Username(String username);
}
