package com.getarrays.entity;

import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;

@Entity
public class Invoice implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false, updatable = false)
    private Long id;

    @ApiModelProperty(notes = "Unique number for an invoice", example = "123")
    private String invoiceNumber;

    @ApiModelProperty(notes = "List of products in the invoice", example = "[rice, beans]")
    private String[] products;

    @ApiModelProperty(notes = "Name of the customer for the invoice", example = "John Doe")
    private String customer;

    @ApiModelProperty(notes = "The total amount of the invoice", example = "25.78")
    private double total;

    // 🔥 新增：一张 Invoice 对应多个 Payment
    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL)
    private Collection<Payment> payments;

    public Invoice() {}

    public Invoice(String invoiceNumber, String[] products, String customer, double total) {
        this.invoiceNumber = invoiceNumber;
        this.products = products;
        this.customer = customer;
        this.total = total;
    }

    // ===== 原有 getter / setter =====

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String[] getProducts() {
        return products;
    }

    public void setProducts(String[] products) {
        this.products = products;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    // ===== 新增 getter / setter =====

    public Collection<Payment> getPayments() {
        return payments;
    }

    public void setPayments(Collection<Payment> payments) {
        this.payments = payments;
    }
}
