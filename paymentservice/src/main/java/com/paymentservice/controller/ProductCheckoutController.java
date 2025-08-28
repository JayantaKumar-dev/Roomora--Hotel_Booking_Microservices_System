package com.paymentservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.paymentservice.dto.ProductRequest;
import com.paymentservice.dto.RefundRequest;
import com.paymentservice.dto.RefundResponse;
import com.paymentservice.dto.StripeResponse;
import com.paymentservice.service.StripeService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;


@RestController
@RequestMapping("/product/v1")
public class ProductCheckoutController {


    private StripeService stripeService;

    public ProductCheckoutController(StripeService stripeService) {
        this.stripeService = stripeService;
    }

    @PostMapping("/checkout")
    public StripeResponse checkoutProducts(@RequestBody ProductRequest productRequest) {
        StripeResponse stripeResponse = stripeService.checkoutProducts(productRequest);

        return stripeResponse;     
    }
    
    @GetMapping("/success")
    public String handleSuccess(@RequestParam("session_id") String sessionId) {
        Stripe.apiKey = "<Your_Stripe_secret_key_here>"; // Replace with your actual secret key

        try {
            Session session = Session.retrieve(sessionId);
            String paymentStatus = session.getPaymentStatus();

            if ("paid".equalsIgnoreCase(paymentStatus)) {
                System.out.println("✅ Payment successful: true");
                return "Payment successful";
            } else {
                System.out.println("❌ Payment not completed: false");
                return "Payment not completed";
            }

        } catch (StripeException e) {
            e.printStackTrace();
            return "Stripe error occurred";
        }
    }


//    @GetMapping("/cancel")
//    public String handleCancel() {
//        System.out.println("❌ Payment cancelled: false");
//        return "Payment cancelled";
//    }
    
    
 // Refund endpoint
    @PostMapping("/refund")
    public RefundResponse refundPayment(@RequestBody RefundRequest refundRequest) {
        return stripeService.refundPayment(refundRequest);
    }
    
}