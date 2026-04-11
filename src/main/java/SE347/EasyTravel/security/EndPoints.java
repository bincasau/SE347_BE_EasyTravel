package SE347.EasyTravel.security;

public class EndPoints {
    public static final String frontend_host = "http://localhost:5173";
    public static final String[] PUBLIC_GET_ENDPOINTS = {
            "/account/login",
            "/hotels",
            "/hotels/**",
            "/tours/**",
            "/tours",
            "/blogs/**",
            "/blogs",
            "/rooms",
            "/rooms/**",
            "/account/active-account",
            "/hotel-bookings/search/findByHotelHotelIdAndRoomRoomId",
            "/notifications/public/list",
            "/custom-reviews",
            "/custom-reviews/**"
    };
    public static final String[] PUBLIC_POST_ENDPOINTS = {
            "/account/login",
            "/account/sign-up",
            "/image/upload",
            "/account/forgot-password/request",
            "/account/forgot-password/confirm"
    };
    public static final String[] AUTH_GET_ENDPOINTS = {
            "/payment",
            "/payment/**",
            "/notifications",
            "/notifications/**"
    };
    public static final String[] USER_POST_ENDPOINTS = {
            "/booking",
            "/booking/**"
    };
    public static final String[] AUTH_POST_ENDPOINTS = {
            "/image/auth",
            "/image/auth/**"
    };
    public static final String[] AUTH_DELETE_ENDPOINTS = {
            "/image/delete",
            "/image/auth",
            "/image/auth/**"
    };
    public static final String[] ADMIN_GET_ENDPOINTS = {
            "/users",
            "/users/**",
            "/admin",
            "/admin/**"
    };
    public static final String[] TG_GET_ENDPOINTS = {
            "/tour_guide",
            "/tour_guide/**"
    };
    public static final String[] HM_GET_ENDPOINTS = {
            "/hotel_manager",
            "/hotel_manager/**",
            "/users/search/findUserByBookingId",
            "/users/search/findUserByBookingId/**",
    };
    public static final String[] HM_POST_ENDPOINTS = {
            "/hotel_manager",
            "/hotel_manager/**",
    };
    public static final String[] HM_PUT_ENDPOINTS = {
            "/hotel_manager",
            "/hotel_manager/**",
    };
    public static final String[] HM_DELETE_ENDPOINTS = {
            "/hotel_manager",
            "/hotel_manager/**",
    };
    public static final String[] ADMIN_POST_ENDPOINTS = {
            "/admin",
            "/admin/**",
            "/users",
            "/users/**",
    };
    public static final String[] ADMIN_DELETE_ENDPOINTS = {
            "/admin",
            "/admin/**",
            "/users",
            "/users/**",
    };
    public static final String[] ADMIN_PUT_ENDPOINTS = {
            "/admin",
            "/admin/**",
            "/users",
            "/users/**",
    };

}
