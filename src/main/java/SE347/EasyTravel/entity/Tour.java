package SE347.EasyTravel.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Date;
import java.util.List;

@Entity
@Getter
@Setter
@ToString(exclude = {"images", "itineraries", "reviews", "tourBookings", "tourGuides"})
@Table(name = "tour")
public class Tour {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tour_id")
    private int tourId;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Lob
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "price_adult", nullable = false)
    private double priceAdult;

    @Column(name = "price_child", nullable = false)
    private double priceChild;

    @Column(name = "percent_discount")
    private double percentDiscount;

    @Column(name = "duration_days", nullable = false)
    private int durationDays;

    @Column(name = "start_date", nullable = false)
    private Date startDate;

    @Column(name = "end_date", nullable = false)
    private Date endDate;

    @Column(name = "departure_location", nullable = false, length = 150)
    private String departureLocation;

    @Column(name = "destination", nullable = false, length = 150)
    private String destination;

    @Column(name = "available_seats", nullable = false)
    private int availableSeats;

    @Column(name = "limit_seats", nullable = false)
    private int limitSeats;

    @Lob
    @Column(name = "main_image", columnDefinition = "LONGTEXT")
    private String mainImage;

    @Column(name = "status", length = 50)
    private String status;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Date createdAt;

    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Image> images;

    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Itinerary> itineraries;

    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Review> reviews;

    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<TourBooking> tourBookings;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "tour_guide_assignment",
            joinColumns = @JoinColumn(name = "tour_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @JsonIgnoreProperties({"assignedTours", "tourBookings", "hotelBookings", "reviews", "comments", "notifications"})
    private List<User> tourGuides;
}
