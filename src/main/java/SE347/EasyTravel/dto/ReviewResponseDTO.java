package SE347.EasyTravel.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Date;

@Data
@AllArgsConstructor
public class ReviewResponseDTO {
    private int reviewId;
    private String comment;
    private int rating; // Số sao
    private Date createdAt;
    private String userName;
    private String userAvatar;
}
