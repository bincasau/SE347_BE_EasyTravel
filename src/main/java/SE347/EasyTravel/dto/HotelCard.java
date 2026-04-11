package SE347.EasyTravel.dto;

public record HotelCard(
        Integer hotelId,
        String name,
        String address,
        Double minRoomPrice,
        String mainImage,
        String phoneNumber,
        String description
) {}
