package SE347.EasyTravel.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Date;
import java.util.List;

@Entity
@Data
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

    @Column(name = "main_image", length = 500)
    private String mainImage;

    @Column(name = "status", length = 50)
    private String status;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Date createdAt;

    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Image> images;

    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Itinerary> itineraries;

    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Review> reviews;

    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<TourBooking> tourBookings;
}
