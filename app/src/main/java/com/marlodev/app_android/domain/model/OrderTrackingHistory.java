package com.marlodev.app_android.domain.model;
import java.time.ZonedDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderTrackingHistory {

    private String status;            // EN_CAMINO
    private ZonedDateTime timestamp;  // 2025-11-14T06:58:28Z
    private String performedBy;       // hugo_dl
}