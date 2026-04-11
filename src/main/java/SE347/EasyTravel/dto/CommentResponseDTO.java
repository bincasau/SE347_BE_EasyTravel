package SE347.EasyTravel.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
public class CommentResponseDTO {
    private int commentId;
    private String content;
    private Timestamp createdAt;
    private String userName;
    private String userAvatar;
}
