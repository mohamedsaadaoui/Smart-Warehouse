package com.saadaoui.smartwarehouse.notification.controller;

import com.saadaoui.smartwarehouse.notification.dto.StockAlertResponse;
import com.saadaoui.smartwarehouse.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public List<StockAlertResponse> getAlerts() {
        return notificationService.getAlerts();
    }

    @GetMapping("/count")
    public long getAlertCount() {
        return notificationService.getAlertCount();
    }

}
