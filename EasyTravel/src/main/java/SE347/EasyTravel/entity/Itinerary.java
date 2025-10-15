package SE347.EasyTravel.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "itinerary")
public class Itinerary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "itinerary_id")
    private int itineraryId;

    @Column(name = "day_number", nullable = false)
    private int dayNumber;

    @Column(name = "activities", columnDefinition = "TEXT")
    private String activities;

    @OneToOne
    @JoinColumn(name = "tour_id", referencedColumnName = "tour_id")
    private Tour tour;


}
