package com.saadaoui.smartwarehouse.notification.service.impl;

import com.saadaoui.smartwarehouse.audit.AuditConstants;
import com.saadaoui.smartwarehouse.audit.service.AuditLogService;
import com.saadaoui.smartwarehouse.auth.entity.User;
import com.saadaoui.smartwarehouse.auth.repository.UserRepository;
import com.saadaoui.smartwarehouse.entity.AppSettings;
import com.saadaoui.smartwarehouse.entity.Notification;
import com.saadaoui.smartwarehouse.entity.Product;
import com.saadaoui.smartwarehouse.exception.ResourceNotFoundException;
import com.saadaoui.smartwarehouse.notification.NotificationTypes;
import com.saadaoui.smartwarehouse.notification.dto.NotificationRequest;
import com.saadaoui.smartwarehouse.notification.dto.NotificationResponse;
import com.saadaoui.smartwarehouse.notification.dto.StockAlertResponse;
import com.saadaoui.smartwarehouse.notification.mapper.NotificationMapper;
import com.saadaoui.smartwarehouse.notification.repository.NotificationRepository;
import com.saadaoui.smartwarehouse.notification.service.NotificationService;
import com.saadaoui.smartwarehouse.product.repository.ProductRepository;
import com.saadaoui.smartwarehouse.settings.repository.AppSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private static final int MAX_ALERTS = 100;

    private final NotificationRepository notificationRepository;

    private final UserRepository userRepository;

    private final NotificationMapper notificationMapper;

    private final SimpMessagingTemplate messagingTemplate;

    private final ProductRepository productRepository;

    private final AppSettingsRepository settingsRepository;

    private final AuditLogService auditLogService;

    @Override
    @Transactional
    public NotificationResponse send(NotificationRequest request, String senderEmail) {

        User sender = findUserByEmail(senderEmail);
        User recipient = userRepository.findById(request.recipientId())
                .orElseThrow(() -> new ResourceNotFoundException("Recipient not found"));

        Notification notification = Notification.builder()
                .sender(sender)
                .recipient(recipient)
                .title(request.title())
                .message(request.message())
                .type(NotificationTypes.USER_MESSAGE)
                .build();

        Notification saved = notificationRepository.save(notification);

        auditLogService.record(AuditConstants.ACTION_CREATE, AuditConstants.ENTITY_USER,
                recipient.getId(), "Sent notification \"" + saved.getTitle() + "\" to " + recipient.getEmail());

        NotificationResponse response = notificationMapper.toResponse(saved);

        messagingTemplate.convertAndSendToUser(
                recipient.getEmail(), "/queue/notifications", response);

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationResponse> getMyNotifications(String recipientEmail, Boolean unreadOnly, Pageable pageable) {

        User recipient = findUserByEmail(recipientEmail);

        if (Boolean.TRUE.equals(unreadOnly)) {
            return notificationRepository
                    .findByRecipientAndIsReadOrderByCreatedAtDesc(recipient, false, pageable)
                    .map(notificationMapper::toResponse);
        }

        return notificationRepository
                .findByRecipientOrderByCreatedAtDesc(recipient, pageable)
                .map(notificationMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount(String recipientEmail) {

        User recipient = findUserByEmail(recipientEmail);

        return notificationRepository.countByRecipientAndIsReadFalse(recipient);
    }

    @Override
    @Transactional
    public NotificationResponse markAsRead(UUID id, String recipientEmail) {

        User recipient = findUserByEmail(recipientEmail);

        Notification notification = notificationRepository.findByIdAndRecipient(id, recipient)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));

        notification.setIsRead(true);

        return notificationMapper.toResponse(notificationRepository.save(notification));
    }

    @Override
    @Transactional
    public void markAllRead(String recipientEmail) {

        User recipient = findUserByEmail(recipientEmail);

        notificationRepository.markAllRead(recipient);
    }

    @Override
    @Transactional
    public void delete(UUID id, String recipientEmail) {

        User recipient = findUserByEmail(recipientEmail);

        Notification notification = notificationRepository.findByIdAndRecipient(id, recipient)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));

        notificationRepository.delete(notification);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockAlertResponse> getAlerts() {

        int threshold = currentThreshold();

        return productRepository
                .findProductsAtOrBelowThreshold(threshold, PageRequest.of(0, MAX_ALERTS))
                .stream()
                .map(NotificationServiceImpl::toAlert)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public long getAlertCount() {

        return productRepository.countProductsAtOrBelowThreshold(currentThreshold());
    }

    private User findUserByEmail(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private int currentThreshold() {

        return settingsRepository.findTopByOrderByIdAsc()
                .filter(settings -> Boolean.TRUE.equals(settings.getNotificationsEnabled()))
                .map(AppSettings::getLowStockThreshold)
                .orElse(-1);
    }

    private static StockAlertResponse toAlert(Product product) {

        return StockAlertResponse.builder()
                .productId(product.getId())
                .productName(product.getName())
                .sku(product.getSku())
                .quantity(product.getQuantity())
                .minStock(product.getMinStock())
                .type(product.getQuantity() == 0 ? "OUT_OF_STOCK" : "LOW_STOCK")
                .createdAt(product.getUpdatedAt())
                .build();
    }

}
