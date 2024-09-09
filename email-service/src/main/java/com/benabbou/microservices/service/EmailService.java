package com.benabbou.microservices.service;

import com.benabbou.microservices.order.event.OrderPlacingEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender javaMailSender;

    @Value("${email.sender.address}")
    private String senderEmail;

    @KafkaListener(topics = "order-placing")
    public void listen(OrderPlacingEvent orderPlacingEvent) {
        log.info("Received message from order-placing topic: {}", orderPlacingEvent);

        // Ensure the event contains an email and order number
        if (orderPlacingEvent.getEmail() == null || orderPlacingEvent.getOrderNumber() == null) {
            log.warn("Invalid order event: missing email or order number");
            return; // Exit if email or order number is missing
        }

        // Asynchronously send the email
        sendOrderConfirmationEmail(orderPlacingEvent);
    }

    @Async // Make email sending asynchronous
    public void sendOrderConfirmationEmail(OrderPlacingEvent orderPlacingEvent) {
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom(senderEmail);
            messageHelper.setTo(orderPlacingEvent.getEmail());
            messageHelper.setSubject(String.format("Order Confirmation - Order No: %s", orderPlacingEvent.getOrderNumber()));
            messageHelper.setText(String.format("""
                            <html>
                                <body>
                                    <p>Dear Customer,</p>
                                    <p>Your order with order number <strong>%s</strong> has been placed successfully.</p>
                                    <br>
                                    <p>Best Regards,</p>
                                    <p>Your Shop</p>
                                </body>
                            </html>
                            """, orderPlacingEvent.getOrderNumber()), true); // HTML content
        };

        try {
            javaMailSender.send(messagePreparator);
            log.info("Order confirmation email sent to {}", orderPlacingEvent.getEmail());
        } catch (MailException e) {
            log.error("Error sending email to {}: {}", orderPlacingEvent.getEmail(), e.getMessage(), e);
        }
    }
}
