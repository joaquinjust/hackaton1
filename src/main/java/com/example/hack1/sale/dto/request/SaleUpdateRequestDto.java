package com.example.hack1.sale.dto.request;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaleUpdateRequestDto {
    @Min(value = 1, message = "Las unidades deben ser al menos 1")
    private Integer units;

    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor que 0")
    private BigDecimal price;
}
