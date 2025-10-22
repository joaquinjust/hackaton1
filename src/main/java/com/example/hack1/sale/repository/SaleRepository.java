package com.example.hack1.sale.repository;

import com.example.hack1.sale.model.Sale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface SaleRepository extends JpaRepository<Sale, Long>, JpaSpecificationExecutor<Sale> {
    @Query("""
    select s from Sale s
    where (:from is null or s.soldAt >= :from) and (:to is null or s.soldAt <= :to)
      and (:branch is null or s.branch = :branch)
  """)
    Page<Sale> search(@Param("from") Instant from, @Param("to") Instant to,
                      @Param("branch") String branch, Pageable pageable);

    @Query("""
    select s from Sale s
    where s.soldAt between :from and :to
      and (:branch is null or s.branch = :branch)
  """)
    List<Sale> findForAggregation(@Param("from") Instant from, @Param("to") Instant to,
                                  @Param("branch") String branch);
}
