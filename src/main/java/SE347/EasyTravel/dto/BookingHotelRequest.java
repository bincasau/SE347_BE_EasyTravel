package SE347.EasyTravel.dto;

import lombok.Data;

import java.sql.Date;

@Data
public class BookingHotelRequest {
    private Date checkInDate;
    private Date checkOutDate;
    private Double totalPrice;
    private int hotelId;
    private int roomID;
    private String gmail;
}
