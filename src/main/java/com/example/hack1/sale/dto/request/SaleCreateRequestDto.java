package com.example.hack1.sale.dto.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaleCreateRequestDto {

    @NotBlank(message = "El SKU no puede estar vacío")
    private String sku;

    @Min(value = 1, message = "Las unidades deben ser al menos 1")
    private int units;

    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor que 0")
    private BigDecimal price;

    @NotBlank(message = "La sucursal no puede estar vacía")
    private String branch;

    @NotNull(message = "Debe indicar la fecha de venta")
    private Instant soldAt;
}