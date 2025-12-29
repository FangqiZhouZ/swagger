package com.getarrays.service;

import com.getarrays.entity.Payment;
import com.getarrays.exception.PaymentNotFoundException;
import com.getarrays.repository.PaymentRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepo paymentRepo; // 模拟数据库层

    @InjectMocks
    private PaymentService paymentService; // 注入 Mock 对象到业务层

    private Payment samplePayment;

    @BeforeEach
    void setUp() {
        // 每个测试前初始化一个样例对象
        samplePayment = new Payment();
        samplePayment.setPaymentNumber("PAY-2024-001");
        // 这里可以根据你的 Payment 实体类补充更多字段，如 setAmount, setCustomer 等
    }

    @Test
    @DisplayName("应该成功保存支付记录")
    void shouldSavePayment() {
        // Arrange
        when(paymentRepo.save(any(Payment.class))).thenReturn(samplePayment);

        // Act
        Payment result = paymentService.savePayment(samplePayment);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getPaymentNumber()).isEqualTo("PAY-2024-001");
        verify(paymentRepo, times(1)).save(samplePayment);
    }

    @Test
    @DisplayName("查询支付记录时，如果存在应返回对象")
    void shouldFindPaymentWhenExists() {
        // Arrange
        when(paymentRepo.findPaymentByPaymentNumber("PAY-2024-001"))
                .thenReturn(Optional.of(samplePayment));

        // Act
        Payment found = paymentService.findPayment("PAY-2024-001");

        // Assert
        assertThat(found).isNotNull();
        assertThat(found.getPaymentNumber()).isEqualTo("PAY-2024-001");
    }

    @Test
    @DisplayName("查询支付记录不存在时，应抛出 PaymentNotFoundException")
    void shouldThrowExceptionWhenPaymentNotFound() {
        // Arrange
        String wrongNumber = "NON-EXISTENT";
        when(paymentRepo.findPaymentByPaymentNumber(wrongNumber))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> paymentService.findPayment(wrongNumber))
                .isInstanceOf(PaymentNotFoundException.class)
                .hasMessageContaining("not found");
    }

    @Test
    @DisplayName("应该获取所有支付记录列表")
    void shouldGetAllPayments() {
        // Arrange
        when(paymentRepo.findAll()).thenReturn(Arrays.asList(samplePayment, new Payment()));

        // Act
        Collection<Payment> payments = paymentService.getPayments();

        // Assert
        assertThat(payments).hasSize(2);
        verify(paymentRepo, times(1)).findAll();
    }

    @Test
    @DisplayName("应该能够成功删除记录")
    void shouldDeletePayment() {
        // Act
        paymentService.deletePayment("PAY-2024-001");

        // Assert
        verify(paymentRepo, times(1)).deletePaymentByPaymentNumber("PAY-2024-001");
    }
}