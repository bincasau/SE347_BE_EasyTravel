package SE347.EasyTravel.dto;

import lombok.Data;

@Data
public class BookingTourRequest {
    private int adults;
    private int children;
    private double totalPrice;
    private String email;
    private int tourId;
}
