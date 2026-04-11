package SE347.EasyTravel.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class MonthlyTourStatsDTO {
    private Integer month;
    private Integer year;
    private Long totalTours;
    private Long activatedTours;
    private Long passedTours;
    private Long cancelledTours;
    private Long totalTicketsSold;
    private Long totalEmptySeats;
    private Double totalRevenue;

    public MonthlyTourStatsDTO(Object month, Object year, Long totalTours, Long activatedTours,
                               Long passedTours, Long cancelledTours, Long totalTicketsSold,
                               Long totalEmptySeats, Double totalRevenue) {
        this.month = (month instanceof Number) ? ((Number) month).intValue() : 0;
        this.year = (year instanceof Number) ? ((Number) year).intValue() : 0;
        this.totalTours = totalTours;
        this.activatedTours = activatedTours;
        this.passedTours = passedTours;
        this.cancelledTours = cancelledTours;
        this.totalTicketsSold = totalTicketsSold;
        this.totalEmptySeats = totalEmptySeats;
        this.totalRevenue = totalRevenue;
    }
}