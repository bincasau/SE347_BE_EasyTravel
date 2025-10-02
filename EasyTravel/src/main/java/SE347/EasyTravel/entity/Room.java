package SE347.EasyTravel.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "room")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private int roomId;

    @Column(name = "room_number", nullable = false)
    private int roomNumber;

    @Column(name = "room_type", nullable = false, length = 100)
    private String roomType;

    @Column(name = "price", nullable = false)
    private double price;

    @Column(name = "number_of_guest")
    private int numberOfGuest;

    @Column(name = "description", columnDefinition = "TEXT")
    private String desc;

    @Column(name = "image_wc", length = 500)
    private String imageWC;

    @Column(name = "image_bed", length = 500)
    private String imageBed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HotelBooking> hotelBookings;
}
