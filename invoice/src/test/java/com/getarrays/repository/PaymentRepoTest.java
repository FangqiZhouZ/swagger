package com.getarrays.repository;

import com.getarrays.entity.Invoice;
import com.getarrays.entity.Payment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
//@ActiveProfiles("test") // 很重要
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // Add this!
class PaymentRepoTest {

    @Autowired
    private PaymentRepo paymentRepo;

    @Autowired
    private InvoiceRepo invoiceRepo;

    @Test
    void shouldSavePaymentWithInvoice() {

        // 1️⃣ 创建 Invoice（父）
        Invoice invoice = new Invoice(
                "INV-TEST-1",
                new String[]{"rice", "beans"},
                "Test User",
                200
        );
        Invoice savedInvoice = invoiceRepo.save(invoice);

        // 2️⃣ 创建 Payment（子）
        Payment payment = new Payment(
                "PAY-TEST-1",
                "Credit Card",
                200,
                "SUCCESS"
        );
        payment.setInvoice(savedInvoice);

        Payment savedPayment = paymentRepo.save(payment);

        // 3️⃣ 断言（验证）
        assertThat(savedPayment.getId()).isNotNull();
        assertThat(savedPayment.getInvoice()).isNotNull();
        assertThat(savedPayment.getInvoice().getInvoiceNumber())
                .isEqualTo("INV-TEST-1");
    }
}

