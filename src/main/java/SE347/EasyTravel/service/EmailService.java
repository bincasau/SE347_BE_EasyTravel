package SE347.EasyTravel.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class EmailService implements EmailInterfaceService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Autowired
    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }


    @Override
    public void sendMessage(String toEmail, String code) {
        try{
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(toEmail);
            helper.setFrom(fromEmail);
            helper.setSubject("Account Verification");
            String content = buildContent(toEmail, code);
            helper.setText(content, true);
            javaMailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
    private String buildContent(String email, String code) {
        String encodedEmail = URLEncoder.encode(email, StandardCharsets.UTF_8);
        String encodedCode = URLEncoder.encode(code, StandardCharsets.UTF_8);
        String verifyLink = frontendUrl + "/verify?email=" + encodedEmail + "&code=" + encodedCode;
        return """
        <html>
        <body style="font-family: Arial, sans-serif; line-height: 1.6;">
            <h2>Hello,</h2>
            <p>Thank you for signing up at <b>EasyTravel</b>.</p>
            <p>Click the button below to activate your account:</p>

            <a href="%s"
               style="display: inline-block; padding: 12px 20px; 
                      background: #ff6b00; color: white; 
                      font-weight: bold; text-decoration: none;
                      border-radius: 6px;">
                Activate Account
            </a>

            <p>If the button doesn't work, copy and paste this link:</p>
            <p><a href="%s">%s</a></p>

            <br/>
            <p>Best regards,<br><b>EasyTravel Team</b></p>
        </body>
        </html>
        """.formatted(verifyLink, verifyLink, verifyLink);
    }
    public void sendPasswordEmail(String toEmail, String newPassword) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(toEmail);
            helper.setFrom(fromEmail);
            helper.setSubject("Code của bạn - EasyTravel");
            String content = """
        <html>
        <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
            <div style="max-width: 600px; margin: auto; border: 1px solid #ddd; padding: 20px; border-radius: 10px;">
                <h2 style="color: #ff6b00;">Cấp code quên mật khẩu!</h2>
                <p>Chào bạn,</p>
                <p>Hệ thống đã tạo code mới cho tài khoản của bạn.</p>
                <div style="background: #f4f4f4; padding: 15px; text-align: center; border-radius: 6px;">
                    <span style="font-size: 24px; font-weight: bold; letter-spacing: 2px; color: #d32f2f;">%s</span>
                </div>
                <p style="margin-top: 20px;"><b>Lưu ý:</b></p>
                <ul>
                    <li>Vui lòng nhập mã code <b>đổi lại mật khẩu ngay lập tức</b> để đảm bảo an toàn.</li>
                </ul>
                <p>Trân trọng,<br><b>EasyTravel Team</b></p>
            </div>
        </body>
        </html>
        """.formatted(newPassword);
            helper.setText(content, true);
            javaMailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Lỗi gửi mail: " + e.getMessage());
        }
    }
    public void sendTourNotificationEmail(String toEmail, String subject, String messageContent) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(toEmail);
            helper.setFrom(fromEmail);
            helper.setSubject(subject);

            String htmlContent = String.format("""
        <html>
        <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
            <div style="max-width: 600px; margin: auto; border: 1px solid #ddd; padding: 20px; border-radius: 10px;">
                <h2 style="color: #ff6b00;">Thông báo lịch trình Tour</h2>
                <p>%s</p>
                <br/>
                <p>Trân trọng,<br><b>EasyTravel Team</b></p>
            </div>
        </body>
        </html>
        """, messageContent);

            helper.setText(htmlContent, true);
            javaMailSender.send(message);
        } catch (MessagingException e) {
            logger.error("Error sending tour notification email", e);
        }
    }
}
