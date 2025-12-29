package com.getarrays.service;

import com.getarrays.entity.Invoice;
import com.getarrays.exception.InvoiceNotFoundException;
import com.getarrays.repository.InvoiceRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collection;

@Service
@Transactional
public class InvoiceService {
    private final InvoiceRepo invoiceRepo;

    @Autowired
    public InvoiceService(InvoiceRepo invoiceRepo) {
        this.invoiceRepo = invoiceRepo;
    }

    public Invoice saveInvoice(Invoice invoice) {
        return invoiceRepo.save(invoice);
    }

    public Collection<Invoice> getInvoices() {
        return invoiceRepo.findAll();
    }

    public Invoice findInvoice(String invoiceNumber) {
        return invoiceRepo.findInvoiceByInvoiceNumber(invoiceNumber).orElseThrow(() ->
                new InvoiceNotFoundException("Invoice by number " + invoiceNumber + " was not found"));
    }

    public Invoice updateInvoice(Invoice invoice) {
        return invoiceRepo.save(invoice);
    }

    public void deleteInvoice(String invoiceNumber) {
        invoiceRepo.deleteInvoiceByInvoiceNumber(invoiceNumber);
    }

    public Collection<Invoice> findInvoicesByCustomer(String customer) {
        return invoiceRepo.findInvoicesByCustomer(customer);
    }
}
