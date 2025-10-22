package com.example.hack1.sale.model;


import com.example.hack1.sale.dto.request.SaleCreateRequestDto;
import com.example.hack1.sale.dto.request.SaleUpdateRequestDto;
import com.example.hack1.sale.dto.response.SaleResponseDto;
import com.example.hack1.sale.repository.SaleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.*;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class SaleService {

    private final SaleRepository saleRepository;

    public SaleResponseDto create(SaleCreateRequestDto r, CurrentUser me) {
        String branchToUse = (me.getRole() == Role.CENTRAL) ? r.getBranch() : me.getBranch();
        if (me.getRole() == Role.BRANCH && (branchToUse == null || !branchToUse.equals(me.getBranch()))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puedes crear ventas para otra sucursal.");
        }
        if (branchToUse == null || branchToUse.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La sucursal es obligatoria.");
        }

        Sale s = new Sale();
        s.setSku(requireNotBlank(r.getSku(), "sku"));
        s.setUnits(requireMin(r.getUnits(), 1, "units"));
        s.setPrice(requirePositive(r.getPrice(), "price"));
        s.setBranch(branchToUse);
        s.setSoldAt(requireNotNullInstant(r.getSoldAt(), "soldAt"));
        saleRepository.save(s);

        return toResponse(s);
    }

    public SaleResponseDto getById(Long id, CurrentUser me) {
        Sale s = saleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Venta no encontrada."));
        enforceReadAccess(s, me);
        return toResponse(s);
    }

    public Page<SaleResponseDto> list(LocalDate from, LocalDate to, String branch, Pageable pageable, CurrentUser me) {
        String effectiveBranch = (me.getRole() == Role.CENTRAL) ? branch : me.getBranch();

        Instant iFrom = (from == null)
                ? Instant.EPOCH
                : from.atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant iTo = (to == null)
                ? Instant.now()
                : to.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant(); // inclusive

        return saleRepository.search(iFrom, iTo, effectiveBranch, pageable)
                .map(this::toResponse);
    }

    public SaleResponseDto update(Long id, SaleUpdateRequestDto r, CurrentUser me) {
        Sale s = saleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Venta no encontrada."));
        enforceWriteAccess(s, me);

        if (r.getUnits() != null) {
            s.setUnits(requireMin(r.getUnits(), 1, "units"));
        }
        if (r.getPrice() != null) {
            s.setPrice(requirePositive(r.getPrice(), "price"));
        }
        saleRepository.save(s);
        return toResponse(s);
    }

    public void delete(Long id, CurrentUser me) {
        if (me.getRole() != Role.CENTRAL) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solo CENTRAL puede eliminar ventas.");
        }
        if (!saleRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Venta no encontrada.");
        }
        saleRepository.deleteById(id);
    }

    // -------- Helpers --------
    private void enforceReadAccess(Sale s, CurrentUser me) {
        if (me.getRole() == Role.BRANCH && !Objects.equals(s.getBranch(), me.getBranch())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puedes ver ventas de otra sucursal.");
        }
    }

    private void enforceWriteAccess(Sale s, CurrentUser me) {
        if (me.getRole() == Role.BRANCH && !Objects.equals(s.getBranch(), me.getBranch())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puedes modificar ventas de otra sucursal.");
        }
    }

    private String requireNotBlank(String v, String field){
        if (v == null || v.isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, field + " es obligatorio.");
        return v;
    }

    private int requireMin(int v, int min, String field){
        if (v < min) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, field + " debe ser ≥ " + min);
        return v;
    }

    private BigDecimal requirePositive(BigDecimal val, String field) {
        if (val == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, field + " es obligatorio.");
        if (val.compareTo(BigDecimal.ZERO) <= 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, field + " debe ser > 0");
        return val;
    }

    private Instant requireNotNullInstant(Instant val, String field) {
        if (val == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, field + " es obligatorio.");
        return val;
    }

    private SaleResponseDto toResponse(Sale s) {
        return SaleResponseDto.builder()
                .id(s.getId())
                .sku(s.getSku())
                .units(s.getUnits())
                .price(s.getPrice())
                .branch(s.getBranch())
                .soldAt(s.getSoldAt())
                .build();
    }
}

