package SE347.EasyTravel.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
@ToString(exclude = {"tourBooking", "hotelBooking"})
@Table(name = "payment")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private int paymentId;

    @Column(name = "total_price", nullable = false)
    private double totalPrice;

    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "method", length = 50)
    private String method;

    @Column(name = "transaction_code", unique = true, length = 100)
    private String transactionCode;

    @CreationTimestamp
    @Column(name = "payment_date", updatable = false)
    private String paymentDate;

    @OneToOne
    @JoinColumn(name = "tour_booking_id")
    @JsonIgnore
    private TourBooking tourBooking;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "hotel_booking_id")
    @JsonBackReference
    private HotelBooking hotelBooking;
}
