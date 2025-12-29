package com.getarrays.repository;

import com.getarrays.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepo extends JpaRepository<Payment, Long> {

    Optional<Payment> findPaymentByPaymentNumber(String paymentNumber);

    void deletePaymentByPaymentNumber(String paymentNumber);
}
