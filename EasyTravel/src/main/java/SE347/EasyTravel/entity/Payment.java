package SE347.EasyTravel.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Date;

@Entity
@Data
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
    private Date paymentDate;

    @OneToOne
    @JoinColumn(name = "booking_id", nullable = false)
    private TourBooking tourBooking;

    @OneToOne
    @JoinColumn(name = "hotel_booking_id")
    private HotelBooking hotelBooking;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "promotion_id", nullable = true)
    private Promotion promotion;
}
