package com.getarrays.service;

// 引入 Payment 实体类
import com.getarrays.entity.Payment;


import com.getarrays.repository.PaymentRepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collection;

import com.getarrays.exception.PaymentNotFoundException;

// @Service：告诉 Spring，这是一个业务层组件，会被 Spring 扫描并管理
@Service

// @Transactional：这个类里的方法默认运行在事务中（数据库操作要用）
@Transactional
public class PaymentService {

    // 业务层依赖数据访问层（Repository）
    private final PaymentRepo paymentRepo;

    // 构造器注入（推荐写法）
    @Autowired
    public PaymentService(PaymentRepo paymentRepo) {
        this.paymentRepo = paymentRepo;
    }

    // 新增支付记录
    public Payment savePayment(Payment payment) {
        return paymentRepo.save(payment);
    }

    // 更新支付记录（JPA 的 save 既可以新增也可以更新）
    public Payment updatePayment(Payment payment) {
        return paymentRepo.save(payment);
    }

    // 查询所有支付记录
    public Collection<Payment> getPayments() {
        return paymentRepo.findAll();
    }

    // 根据 paymentNumber 查询单个支付


    public Payment findPayment(String paymentNumber) {
        return paymentRepo.findPaymentByPaymentNumber(paymentNumber)
                .orElseThrow(() ->
                        new PaymentNotFoundException(
                                "Payment with number " + paymentNumber + " not found"
                        )
                );
    }


    // 根据 paymentNumber 删除支付
    public void deletePayment(String paymentNumber) {
        paymentRepo.deletePaymentByPaymentNumber(paymentNumber);
    }
}
