package SE347.EasyTravel.controller;

import SE347.EasyTravel.dto.PaymentDTO;
import SE347.EasyTravel.exception.UnauthorizedException;
import SE347.EasyTravel.response.ResponseObject;
import SE347.EasyTravel.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Value("${app.frontend-url:http://localhost:5173}")
    private String frontendUrl;

    @GetMapping("/vn-pay")
    public ResponseObject<PaymentDTO.VNPayResponse> pay(HttpServletRequest request) {
        return new ResponseObject<>(
                HttpStatus.OK,
                "Success",
                paymentService.createVnPayPayment(request)
        );
    }

    @GetMapping("/vn-pay-callback")
    public void payCallbackHandler(HttpServletRequest request, HttpServletResponse response) throws IOException {

        paymentService.processVnPayCallback(request);

        String code = request.getParameter("vnp_ResponseCode");
        String txnRef = request.getParameter("vnp_TxnRef");
        String amount = request.getParameter("vnp_Amount");
        String orderInfo = request.getParameter("vnp_OrderInfo");

        boolean success = "00".equals(code);

        String redirectUrl = frontendUrl
                + "/payment-result"
                + "?status=" + (success ? "success" : "failed")
                + "&code=" + url(code)
                + "&txnRef=" + url(txnRef)
                + "&amount=" + url(amount)
                + "&orderInfo=" + url(orderInfo);

        response.sendRedirect(redirectUrl);
    }

    private String url(String v) {
        if (v == null) return "";
        return URLEncoder.encode(v, StandardCharsets.UTF_8);
    }
    @PostMapping("/refund/{paymentId}")
    public ResponseEntity<?> refund(
            @PathVariable Integer paymentId,
            HttpServletRequest request,
            Principal principal
    ) {
        try {
            String currentUsername = principal.getName();
            String result = paymentService.processRefund(paymentId, request, currentUsername);

            if (result.contains("thành công")) {
                return ResponseEntity.ok(Map.of("status", "success", "message", result));
            } else {
                return ResponseEntity.badRequest().body(Map.of("status", "failed", "message", result));
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    @PostMapping("/refund/{bookingType}/{bookingId}")
    public ResponseEntity<?> refund(
            @PathVariable String bookingType, // "TOUR" hoặc "HOTEL"
            @PathVariable Integer bookingId,
            HttpServletRequest request,
            Principal principal
    ) {
        try {
            if (principal == null) throw new UnauthorizedException("Chưa đăng nhập");

            String currentUsername = principal.getName();

            String result = paymentService.processRefundByBooking(bookingType, bookingId, request, currentUsername);

            if (result.contains("thành công")) {
                return ResponseEntity.ok(Map.of("status", "success", "message", result));
            } else {
                return ResponseEntity.badRequest().body(Map.of("status", "failed", "message", result));
            }
        } catch (Exception e) {
             throw new RuntimeException(e.getMessage());
        }
    }
}
