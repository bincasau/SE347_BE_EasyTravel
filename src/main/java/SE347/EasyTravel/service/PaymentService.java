package SE347.EasyTravel.service;

import SE347.EasyTravel.Config.VNPayConfig;
import SE347.EasyTravel.dao.PaymentRepo;
import SE347.EasyTravel.dao.UserRepo;
import SE347.EasyTravel.dto.PaymentDTO;
import SE347.EasyTravel.entity.*;
import SE347.EasyTravel.utils.VNPayUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);
        private final VNPayConfig vnPayConfig;
        private final BookingService bookingService;
        private final PaymentRepo paymentRepo;
        private final UserRepo userRepo;

        @Autowired
        public PaymentService(VNPayConfig vnPayConfig, BookingService bookingService, PaymentRepo paymentRepo, UserRepo userRepo) {
            this.vnPayConfig = vnPayConfig;
            this.bookingService = bookingService;
            this.paymentRepo = paymentRepo;
            this.userRepo = userRepo;
        }

        public PaymentDTO.VNPayResponse createVnPayPayment(HttpServletRequest request) {
            long amount = Integer.parseInt(request.getParameter("amount")) * 100L;
            String bankCode = request.getParameter("bankCode");
            String bookingId = request.getParameter("bookingId");
            String bookingType = request.getParameter("bookingType");
            Map<String, String> vnpParamsMap = vnPayConfig.getVNPayConfig();

            vnpParamsMap.put("vnp_Amount", String.valueOf(amount));
            vnpParamsMap.put("vnp_TxnRef", bookingId);
            vnpParamsMap.put("vnp_OrderInfo", bookingType + ": pay for #" + bookingId);

            if (bankCode != null && !bankCode.isEmpty()) {
                vnpParamsMap.put("vnp_BankCode", bankCode);
            }
            vnpParamsMap.put("vnp_IpAddr", VNPayUtil.getIpAddress(request));
            String queryUrl = VNPayUtil.getPaymentURL(vnpParamsMap, true);
            String hashData = VNPayUtil.getPaymentURL(vnpParamsMap, false);
            String vnpSecureHash = VNPayUtil.hmacSHA512(vnPayConfig.getSecretKey(), hashData);
            queryUrl += "&vnp_SecureHash=" + vnpSecureHash;
            String paymentUrl = vnPayConfig.getVnp_PayUrl() + "?" + queryUrl;
            return PaymentDTO.VNPayResponse.builder()
                    .code("ok")
                    .message("success")
                    .paymentUrl(paymentUrl).build();
        }

        @Transactional
        public void processVnPayCallback(HttpServletRequest request) {
            String vnpResponseCode = request.getParameter("vnp_ResponseCode");
            String vnpTxnRef = request.getParameter("vnp_TxnRef");
            String vnpTransactionNo = request.getParameter("vnp_TransactionNo");
            long amount = Long.parseLong(request.getParameter("vnp_Amount")) / 100;
            String bankCode = request.getParameter("vnp_BankCode");
            String orderInfo = request.getParameter("vnp_OrderInfo");
            String vnpPayDate = request.getParameter("vnp_PayDate"); // Quan trọng cho Refund
            String bookingType = orderInfo.split(":")[0];

            String status = vnpResponseCode.equals("00") ? "Success" : "Failed";

            try {
                int bookingId = Integer.parseInt(vnpTxnRef);
                Payment payment = new Payment();
                payment.setTotalPrice((double) amount);
                payment.setStatus(status);
                payment.setMethod("VNPay - " + bankCode);
                payment.setTransactionCode(vnpTransactionNo);
                payment.setPaymentDate(vnpPayDate);

                if ("TOUR".equalsIgnoreCase(bookingType)) {
                    TourBooking tourBooking = bookingService.findTourBookingById(bookingId);
                    if (tourBooking != null) {
                        bookingService.updateTourBookingStatus(tourBooking, status);
                        payment.setTourBooking(tourBooking);
                    }
                } else if ("HOTEL".equalsIgnoreCase(bookingType)) {
                    HotelBooking hotelBooking = bookingService.findHotelBookingById(bookingId);
                    if (hotelBooking != null) {
                        bookingService.updateHotelBookingStatus(hotelBooking, status);
                        payment.setHotelBooking(hotelBooking);
                    }
                }

                if (payment.getTourBooking() != null || payment.getHotelBooking() != null) {
                    paymentRepo.save(payment);
                }
            } catch (Exception e) {
                logger.error("Error processing VNPay callback", e);
            }
        }

        @Transactional
        public String processRefund(Integer paymentId, HttpServletRequest request, String currentUsername) throws Exception {
            Payment payment = paymentRepo.findById(paymentId)
                    .orElseThrow(() -> new RuntimeException("Giao dịch không tồn tại"));

            String ownerUsername = "";
            if (payment.getTourBooking() != null) {
                ownerUsername = payment.getTourBooking().getUser().getUsername();
            } else if (payment.getHotelBooking() != null) {
                ownerUsername = payment.getHotelBooking().getUser().getUsername();
            }
            if (!currentUsername.equals(ownerUsername)) {
                return "Bạn không có quyền yêu cầu hoàn tiền cho giao dịch này!";
            }
            String validateMsg = validateRefund(payment);
            if (!validateMsg.equals("OK")) return validateMsg;

            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
            String vnp_CreateDate = formatter.format(cld.getTime());

            String vnp_RequestId = VNPayUtil.getRandomNumber(8);
            String vnp_TransactionNo = payment.getTransactionCode();
            String vnp_TransactionDate = payment.getPaymentDate().replaceAll("[^0-9]", "");
            vnp_TransactionDate = vnp_TransactionDate.length() >= 14 ? vnp_TransactionDate.substring(0, 14) : vnp_TransactionDate;
//            String vnp_TxnRef = String.valueOf(payment.getPaymentId());
            String vnp_TxnRef;
            if (payment.getTourBooking() != null) {
                vnp_TxnRef = String.valueOf(payment.getTourBooking().getBookingId());
            } else if (payment.getHotelBooking() != null) {
                vnp_TxnRef = String.valueOf(payment.getHotelBooking().getBookingId());
            } else {
                throw new RuntimeException("Payment không gắn với TourBooking hoặc HotelBooking");
            }

            long amount = (long) (payment.getTotalPrice() * 100);
            String vnp_IpAddr = VNPayUtil.getIpAddress(request);
            String vnp_OrderInfo = "Refund_" + (payment.getTourBooking() != null ? "TOUR" : "HOTEL") + "_" + vnp_TxnRef;

            String hashData = vnp_RequestId + "|2.1.0|refund|" + vnPayConfig.getVnp_TmnCode() + "|02|" +
                    vnp_TxnRef + "|" + amount + "|" + vnp_TransactionNo + "|" +
                    vnp_TransactionDate + "|" + currentUsername + "|" + vnp_CreateDate + "|" + vnp_IpAddr + "|" + vnp_OrderInfo;

            String vnp_SecureHash = VNPayUtil.hmacSHA512(vnPayConfig.getSecretKey(), hashData);

            // --- 5. GỬI REQUEST SERVER-TO-SERVER ---
            Map<String, Object> body = new HashMap<>();
            body.put("vnp_RequestId", vnp_RequestId);
            body.put("vnp_Version", "2.1.0");
            body.put("vnp_Command", "refund");
            body.put("vnp_TmnCode", vnPayConfig.getVnp_TmnCode());
            body.put("vnp_TransactionType", "02");
            body.put("vnp_TxnRef", vnp_TxnRef);
            body.put("vnp_Amount", amount);
            body.put("vnp_OrderInfo", vnp_OrderInfo);
            body.put("vnp_TransactionNo", vnp_TransactionNo);
            body.put("vnp_TransactionDate", vnp_TransactionDate);
            body.put("vnp_CreateBy", currentUsername);
            body.put("vnp_CreateDate", vnp_CreateDate);
            body.put("vnp_IpAddr", vnp_IpAddr);
            body.put("vnp_SecureHash", vnp_SecureHash);

            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.postForObject(vnPayConfig.getVnp_ApiUrl(), body, String.class);

            // --- 6. XỬ LÝ KẾT QUẢ ---
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);
            String responseCode = root.get("vnp_ResponseCode").asText();
            if ("00".equals(responseCode)) {
                payment.setStatus("Refunded");

                if (payment.getTourBooking() != null) {
                    bookingService.updateTourBookingStatus(payment.getTourBooking(), "Cancelled");
                    Tour tour = payment.getTourBooking().getTour();
                    int seats = payment.getTourBooking().getAdults() + payment.getTourBooking().getChildren();
                    tour.setAvailableSeats(tour.getAvailableSeats() + seats);
                } else if (payment.getHotelBooking() != null) {
                    bookingService.updateHotelBookingStatus(payment.getHotelBooking(), "Cancelled");
                }
                paymentRepo.save(payment);
                return "Hoàn tiền thành công!";
            } else {
                return "VNPay từ chối hoàn tiền. Mã lỗi: " + responseCode;
            }
        }

        @Transactional
        public String processRefundByBooking(String bookingType, Integer bookingId, HttpServletRequest request, String currentUsername) throws Exception {

            Payment payment;
            if ("TOUR".equalsIgnoreCase(bookingType)) {
                payment = paymentRepo.findByTourBooking_BookingId(bookingId)
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy thanh toán cho Tour này"));
            } else if ("HOTEL".equalsIgnoreCase(bookingType)) {
                payment = paymentRepo.findByHotelBooking_BookingId(bookingId)
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy thanh toán cho Khách sạn này"));
            } else {
                return "Loại dịch vụ không hợp lệ (Phải là TOUR hoặc HOTEL)";
            }

            String ownerUsername = ("TOUR".equalsIgnoreCase(bookingType))
                    ? payment.getTourBooking().getUser().getUsername()
                    : payment.getHotelBooking().getUser().getUsername();
            User currentUser = userRepo.findByUsername(currentUsername);
            boolean isAdmin = currentUser != null && "ADMIN".equalsIgnoreCase(currentUser.getRole());

            if (!currentUsername.equals(ownerUsername) && !isAdmin) {
                return "Bạn không có quyền yêu cầu hoàn tiền cho đơn hàng này!";
            }

            String validateMsg = validateRefund(payment);
            if (!validateMsg.equals("OK")) return validateMsg;


            Thread.sleep(1000);

            String fakeResponseCode = "00";

            if ("00".equals(fakeResponseCode)) {
                // 1. Cập nhật trạng thái Payment sang 'Refunded'
                payment.setStatus("Refunded");

                if ("TOUR".equalsIgnoreCase(bookingType)) {
                    // 2. Cập nhật trạng thái TourBooking sang 'Cancelled'
                    bookingService.updateTourBookingStatus(payment.getTourBooking(), "Cancelled");

                    // 3. Hoàn trả số ghế trống vào Tour (Sử dụng trường adults + children từ DB)
                    Tour tour = payment.getTourBooking().getTour();
                    int seatsToReturn = payment.getTourBooking().getAdults() + payment.getTourBooking().getChildren();
                    tour.setAvailableSeats(tour.getAvailableSeats() + seatsToReturn);

                    logger.info("Refunded {} seats for tour: {}", seatsToReturn, tour.getTitle());
                } else {
                    // 2. Cập nhật trạng thái HotelBooking sang 'Cancelled'
                    bookingService.updateHotelBookingStatus(payment.getHotelBooking(), "Cancelled");
                }

                // 4. Lưu các thay đổi vào Database
                paymentRepo.save(payment);

                return "Hoàn tiền " + bookingType + " thành công!";
            } else {
                return "Lỗi VNPay (Giả lập thất bại)";
            }
        }

        public String validateRefund(Payment payment) {
            long now = System.currentTimeMillis();
            if (payment.getTourBooking() != null) {
                long startDate = payment.getTourBooking().getTour().getStartDate().getTime();
                if (startDate - now < 3L * 24 * 60 * 60 * 1000) {
                    return "Tour chỉ được hoàn tiền trước 3 ngày khởi hành.";
                }
            }
            if (payment.getHotelBooking() != null) {
                long checkInDate = payment.getHotelBooking().getCheckInDate().getTime();
                if (checkInDate - now < 12L * 60 * 60 * 1000) {
                    return "Khách sạn chỉ được hoàn tiền trước 12 giờ nhận phòng.";
                }
            }
            return "OK";
        }

    }