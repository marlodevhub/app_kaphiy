package com.marlodev.app_android.domain.model;
import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Extra {
    private Long id;
    private String name;
    private BigDecimal price;
}