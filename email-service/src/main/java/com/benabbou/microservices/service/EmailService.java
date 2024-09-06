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
        log.info("Got Message from order-placing topic: {}", orderPlacingEvent);

        // Ensure the event contains an email and order number
        if (orderPlacingEvent.getEmail() == null || orderPlacingEvent.getOrderNumber() == null) {
            log.warn("Invalid order event: missing email or order number");
            return; // Exit if email or order number is missing
        }

        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom(senderEmail);
            messageHelper.setTo(orderPlacingEvent.getEmail());
            messageHelper.setSubject(String.format("Your Order with Order Number %s is placed successfully", orderPlacingEvent.getOrderNumber()));
            messageHelper.setText(String.format("""
                            Hi,

                            Your order with order number %s is now placed successfully.

                            Best Regards,
                            Your Shop
                            """, orderPlacingEvent.getOrderNumber()), true);
        };

        try {
            javaMailSender.send(messagePreparator);
            log.info("Order notification email sent successfully!");
        } catch (MailException e) {
            log.error("Exception occurred when sending mail to {}", orderPlacingEvent.getEmail(), e);
            throw new RuntimeException("Exception occurred when sending mail to " + orderPlacingEvent.getEmail(), e);
        }
    }
}
