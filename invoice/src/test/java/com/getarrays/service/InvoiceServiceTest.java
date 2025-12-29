package com.getarrays.service;

import com.getarrays.entity.Invoice;
import com.getarrays.exception.InvoiceNotFoundException;
import com.getarrays.repository.InvoiceRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Initializes Mockito
class InvoiceServiceTest {

    @Mock
    private InvoiceRepo invoiceRepo; // Mocks the dependency

    @InjectMocks
    private InvoiceService invoiceService; // Injects the mock into the service

    private Invoice sampleInvoice;

    @BeforeEach
    void setUp() {
        sampleInvoice = new Invoice(
                "INV-001",
                new String[]{"Laptop", "Mouse"},
                "John Doe",
                1200
        );
    }

    @Test
    void shouldSaveInvoiceSuccessfully() {
        // Arrange
        when(invoiceRepo.save(any(Invoice.class))).thenReturn(sampleInvoice);

        // Act
        Invoice savedInvoice = invoiceService.saveInvoice(sampleInvoice);

        // Assert
        assertThat(savedInvoice).isNotNull();
        assertThat(savedInvoice.getInvoiceNumber()).isEqualTo("INV-001");
        verify(invoiceRepo, times(1)).save(sampleInvoice);
    }

    @Test
    void shouldFindInvoiceByNumber() {
        // Arrange
        when(invoiceRepo.findInvoiceByInvoiceNumber("INV-001"))
                .thenReturn(Optional.of(sampleInvoice));

        // Act
        Invoice foundInvoice = invoiceService.findInvoice("INV-001");

        // Assert
        assertThat(foundInvoice.getCustomer()).isEqualTo("John Doe");
    }

    @Test
    void shouldThrowExceptionWhenInvoiceNotFound() {
        // Arrange
        when(invoiceRepo.findInvoiceByInvoiceNumber("NON-EXISTENT"))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> invoiceService.findInvoice("NON-EXISTENT"))
                .isInstanceOf(InvoiceNotFoundException.class)
                .hasMessageContaining("was not found");
    }

    @Test
    void shouldDeleteInvoice() {
        // Act
        invoiceService.deleteInvoice("INV-001");

        // Assert
        verify(invoiceRepo, times(1)).deleteInvoiceByInvoiceNumber("INV-001");
    }

    @Test
    void shouldFindInvoicesByCustomer() {
        // Arrange
        java.util.List<Invoice> invoiceList = java.util.Arrays.asList(sampleInvoice);
        when(invoiceRepo.findInvoicesByCustomer("John Doe"))
                .thenReturn(invoiceList);

        // Act - Explicitly use Collection<Invoice> instead of var
        java.util.Collection<Invoice> results = invoiceService.findInvoicesByCustomer("John Doe");

        // Assert
        assertThat(results).hasSize(1);
        assertThat(results.iterator().next().getCustomer()).isEqualTo("John Doe");
    }
}