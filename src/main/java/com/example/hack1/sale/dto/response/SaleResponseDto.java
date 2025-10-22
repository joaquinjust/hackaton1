package com.example.hack1.sale.dto.response;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaleResponseDto {
    private Long id;
    private String sku;
    private int units;
    private BigDecimal price;
    private String branch;
    private Instant soldAt;
}
