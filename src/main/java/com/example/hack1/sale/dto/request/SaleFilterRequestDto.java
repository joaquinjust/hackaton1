package com.example.hack1.sale.dto.request;
import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class SaleFilterRequestDto {
    private LocalDate from;
    private LocalDate to;
    private String branch;
    private int page = 0;
    private int size = 10;
}
