package SE347.EasyTravel.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingResponse {
    private int bookingId;
    private String bookingType; // "TOUR" or "HOTEL"
}

