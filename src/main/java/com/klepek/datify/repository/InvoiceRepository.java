package com.klepek.datify.repository;

import com.klepek.datify.entity.Invoice;
import com.klepek.datify.entity.InvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    Optional<Invoice> findByDocumentId(Long documentId);

    List<Invoice> findByVendorNameContainingIgnoreCase(String vendorName);

    List<Invoice> findByStatus(InvoiceStatus status);

    List<Invoice> findByDueDateBefore(LocalDate date);

    List<Invoice> findByDueDateBetween(LocalDate startDate, LocalDate endDate);

    List<Invoice> findByTotalAmountBetween(BigDecimal minAmount, BigDecimal maxAmount);

    @Query("SELECT i FROM Invoice i WHERE i.dueDate <= :date AND i.status = 'PENDING'")
    List<Invoice> findOverdueInvoices(@Param("date") LocalDate date);

    @Query("SELECT SUM(i.totalAmount) FROM Invoice i WHERE i.status = :status")
    BigDecimal getTotalAmountByStatus(@Param("status") InvoiceStatus status);

    @Query("SELECT i FROM Invoice i ORDER BY i.dueDate ASC")
    List<Invoice> findAllOrderByDueDateAsc();

    @Query("SELECT COUNT(i) FROM Invoice i WHERE i.status = 'PENDING' AND i.dueDate <= :date")
    Long countOverdueInvoices(@Param("date") LocalDate date);
}