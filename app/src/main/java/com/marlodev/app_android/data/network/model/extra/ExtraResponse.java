package com.marlodev.app_android.data.network.model.extra;

import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExtraResponse {
    private Long id;
    private String name;
    private BigDecimal price;
}