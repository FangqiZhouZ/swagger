package com.getarrays.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// @ResponseStatus：告诉 Spring
// 一旦这个异常被抛出，HTTP 状态码就是 404
@ResponseStatus(HttpStatus.NOT_FOUND)
public class PaymentNotFoundException extends RuntimeException {

    // 构造器：接收错误信息
    public PaymentNotFoundException(String message) {
        super(message);
    }
}
