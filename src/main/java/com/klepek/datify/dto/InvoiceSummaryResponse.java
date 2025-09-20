package com.klepek.datify.dto;

import java.math.BigDecimal;

public class InvoiceSummaryResponse {

    private Long totalInvoices;
    private Long overdueInvoices;
    private BigDecimal totalAmount;
    private BigDecimal overdueAmount;
    private BigDecimal paidAmount;

    public InvoiceSummaryResponse() {
    }

    public InvoiceSummaryResponse(Long totalInvoices, Long overdueInvoices,
                                  BigDecimal totalAmount, BigDecimal overdueAmount,
                                  BigDecimal paidAmount) {
        this.totalInvoices = totalInvoices;
        this.overdueInvoices = overdueInvoices;
        this.totalAmount = totalAmount;
        this.overdueAmount = overdueAmount;
        this.paidAmount = paidAmount;
    }

    // Getters and Setters
    public Long getTotalInvoices() {
        return totalInvoices;
    }

    public void setTotalInvoices(Long totalInvoices) {
        this.totalInvoices = totalInvoices;
    }

    public Long getOverdueInvoices() {
        return overdueInvoices;
    }

    public void setOverdueInvoices(Long overdueInvoices) {
        this.overdueInvoices = overdueInvoices;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getOverdueAmount() {
        return overdueAmount;
    }

    public void setOverdueAmount(BigDecimal overdueAmount) {
        this.overdueAmount = overdueAmount;
    }

    public BigDecimal getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
    }
}
