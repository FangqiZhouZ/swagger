package com.getarrays.entity;

import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class Payment implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false, updatable = false)
    private Long id;

    @ApiModelProperty(notes = "Unique payment number", example = "PAY-001")
    private String paymentNumber;

    @ApiModelProperty(notes = "Payment method", example = "Credit Card")
    private String method;

    @ApiModelProperty(notes = "Amount paid", example = "100.50")
    private double amount;

    @ApiModelProperty(notes = "Payment status", example = "SUCCESS")
    private String status;

    // 🔥 关键新增部分开始 🔥

    // 多个 Payment 对应一个 Invoice
    @ManyToOne
    @JoinColumn(name = "invoice_id") // 数据库中的外键列
    private Invoice invoice;

    // 🔥 关键新增部分结束 🔥

    public Payment() {}

    public Payment(String paymentNumber, String method, double amount, String status) {
        this.paymentNumber = paymentNumber;
        this.method = method;
        this.amount = amount;
        this.status = status;
    }

    // ===== getter & setter =====

    public Long getId() {
        return id;
    }

    public String getPaymentNumber() {
        return paymentNumber;
    }

    public void setPaymentNumber(String paymentNumber) {
        this.paymentNumber = paymentNumber;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }
}
