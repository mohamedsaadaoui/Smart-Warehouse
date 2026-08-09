package com.saadaoui.smartwarehouse.notification.service;

import com.saadaoui.smartwarehouse.notification.dto.StockAlertResponse;

import java.util.List;

public interface NotificationService {

    List<StockAlertResponse> getAlerts();

    long getAlertCount();

}
