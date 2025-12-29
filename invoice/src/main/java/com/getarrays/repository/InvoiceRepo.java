package com.getarrays.repository;

import com.getarrays.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.Optional;

public interface InvoiceRepo extends JpaRepository<Invoice, Long> {
    Optional<Invoice> findInvoiceByInvoiceNumber(String invoiceNumber);
    void deleteInvoiceByInvoiceNumber(String invoiceNumber);
    Collection<Invoice> findInvoicesByCustomer(String customer);



}
