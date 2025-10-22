package com.example.hack1;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)


public class SalesAggregationServiceTest {
    @Mock
    private SaleRepository saleRepository;

    @InjectMocks
    private SalesAggregationServiceTest salesAggregationServiceTest;

    private Sale createSale( String sku, int units, double price, String branch) {
        return new Sale(null, sku, units,price, branch, LocalDate.now());
    }
    @Test
    void shouldCalculateCorrectAggregatesWithValidData() {
        // Given
        List<Sale> mockSales = List.of(
                createSale("OREO_CLASSIC", 10, 1.99, "Miraflores"),
                createSale("OREO_DOUBLE", 5, 2.49, "San Isidro"),
                createSale("OREO_CLASSIC", 15, 1.99, "Miraflores")
        );
        when(saleRepository.findByDateRange(any(), any())).thenReturn(mockSales);

        // When
        SaleAggregates result = saleAggregationService.calculateAggregates(
                LocalDate.now().minusDays(7), LocalDate.now(), null
        );

        // Then
        assertThat(result.getTotalUnits()).isEqualTo(30);
        assertThat(result.getTotalRevenue()).isEqualTo(42.43);
        assertThat(result.getTopSku()).isEqualTo("OREO_CLASSIC");
        assertThat(result.getTopBranch()).isEqualTo("Miraflores");

    }
    @Test
    void ReturnZeroWhenTheSaleListIsEmpty(){
        when(saleRepository.findByDateRange(any(), any())).thenReturn(mockSales);
        SaleAggregates result = saleAggregationService.calculateAggregates(
                LocalDate.now().minusDays(7), LocalDate.now(), null;
        assertThat(result.getTotalUnits()).isZero();
        assertThat(result.getTotalRevenue()).isZero();
        assertThat(result.getTopSku()).isNull();
    }
    @Test
    void shouldIdFilterByBranchCorrectly() {
        // Given
        List<Sale> mockSales = List.of(
                createSale("OREO_CLASSIC", 10, 1.99, "Miraflores"),
                createSale("OREO_DOUBLE", 5, 2.49, "San Isidro")
        );
        when(saleRepository.findByDateRange(any(), any())).thenReturn(mockSales);

        // When
        SaleAggregates result = saleAggregationService.calculateAggregates(
                LocalDate.now().minusDays(7), LocalDate.now(), null;
        assertThat(result.getTotalUnits()).isEqualTo(10);
        assertThat(result.getTopBranch()).isEqualTo("Miraflores");
    }
    @Test
    void shouldIFindByDateRangeCorrectly() {
        // Given
        List<Sale> mockSales = List.of(
                createSale("OREO_CLASSIC", 10, 1.99, "Miraflores");
        when(saleRepository.findByDataRange(any(), any())).thenReturn(mockSales));
        SalesAggregates result = service.calculateAggregates(LocalDate.now().minusDays(2), LocalDate.now(), null);
        assertThat(result.getTotalUnits()).isEqualTo(10);
    }
    @Test
    void shouldHandleTopSkuTieCorrectly() {
        List<Sale> mockSales = List.of(
                createSale("OREO_CLASSIC", 10, 1.99, "Miraflores"),
                createSale("OREO_DOUBLE", 10, 2.49, "San Isidro")
        );
        when(salesRepository.findByDateRange(any(), any())).thenReturn(mockSales);

        SalesAggregates result = service.calculateAggregates(LocalDate.now().minusDays(7), LocalDate.now(), null);

        assertThat(List.of("OREO_CLASSIC", "OREO_DOUBLE")).contains(result.getTopSku());
    }



}



