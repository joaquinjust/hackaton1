package com.example.hack1.sale.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Getter
@Setter

public class Sale {
    @Id @GeneratedValue
    private Long id;

    @Column(nullable=false)
    private String sku;

    @Column(nullable=false)
    private Integer units;

    @Column(nullable=false)
    private BigDecimal price;

    @Column(nullable=false)
    private String branch;

    @Column(nullable=false)
    private Instant soldAt;

    @Column(nullable=false)
    private Instant createdAt = Instant.now();
}
