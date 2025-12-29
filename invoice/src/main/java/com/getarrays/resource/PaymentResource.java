package com.getarrays.resource;

import com.getarrays.constant.SwaggerConstant;
import com.getarrays.entity.Payment;
import com.getarrays.service.PaymentService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/payment")
@Api(tags = { SwaggerConstant.PAYMENT_API_TAG })
public class PaymentResource {

    private final PaymentService paymentService;

    @Autowired
    public PaymentResource(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @ApiOperation(value = "Add a new payment")
    @PostMapping("/add")
    public ResponseEntity<Payment> savePayment(@RequestBody Payment payment) {
        Payment newPayment = paymentService.savePayment(payment);
        return ResponseEntity.ok(newPayment);
    }

    @ApiOperation(value = "Update an existing payment")
    @PutMapping("/update")
    public ResponseEntity<Payment> updatePayment(@RequestBody Payment payment) {
        return ResponseEntity.ok(paymentService.updatePayment(payment));
    }

    @ApiOperation(value = "Get all payments")
    @GetMapping("/all")
    public ResponseEntity<Collection<Payment>> getPayments() {
        return ResponseEntity.ok(paymentService.getPayments());
    }

    @ApiOperation(value = "Get payment by payment number")
    @GetMapping("/{paymentNumber}")
    public ResponseEntity<Payment> getPayment(@PathVariable String paymentNumber) {
        return ResponseEntity.ok(paymentService.findPayment(paymentNumber));
    }

    @ApiOperation(value = "Delete payment by payment number")
    @DeleteMapping("/delete/{paymentNumber}")
    public ResponseEntity<?> deletePayment(@PathVariable String paymentNumber) {
        paymentService.deletePayment(paymentNumber);
        return ResponseEntity.ok().build();
    }
}
