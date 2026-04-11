package SE347.EasyTravel.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Entity
@Getter
@Setter
@ToString(exclude = {"hotel", "hotelBookings"})
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

    @Lob
    @Column(name = "image_wc", columnDefinition = "LONGTEXT")
    private String imageWC;

    @Lob
    @Column(name = "image_bed", columnDefinition = "LONGTEXT")
    private String imageBed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    @JsonIgnore
    private Hotel hotel;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<HotelBooking> hotelBookings;
}
