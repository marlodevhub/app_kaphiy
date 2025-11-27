package com.marlodev.app_android.data.network.mapper;

import com.marlodev.app_android.domain.model.OrderTrackingHistory;
import com.marlodev.app_android.data.network.model.order.OrderTrackingResponse.OrderStatusLog;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class OrderTrackingHistoryMapper {

    public static OrderTrackingHistory fromResponse(OrderStatusLog res) {
        if (res == null) return null;

        ZonedDateTime timestamp = null;
        if (res.getTimestamp() != null) {
            timestamp = ZonedDateTime.parse(res.getTimestamp(), DateTimeFormatter.ISO_DATE_TIME);
        }

        return OrderTrackingHistory.builder()
                .status(formatStatus(res.getStatus()))
                .timestamp(timestamp)
                .performedBy(res.getPerformedBy())
                .build();
    }

    private static String formatStatus(String raw) {
        if (raw == null) return "";
        String s = raw.replace("_", " ").toLowerCase();
        return s.substring(0,1).toUpperCase() + s.substring(1);
    }
}
