package com.paymentservice.service;

import com.paymentservice.dto.ProductRequest;
import com.paymentservice.dto.RefundRequest;
import com.paymentservice.dto.RefundResponse;
import com.paymentservice.dto.StripeResponse;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Refund;
import com.stripe.model.checkout.Session;
import com.stripe.param.RefundCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.param.checkout.SessionRetrieveParams;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StripeService {


    @Value("${stripe.api.secret.key}")
    private String secretKey;

   
    //stripe -API
    //-> productName , amount , quantity , currency
    //-> return sessionId and url



        public StripeResponse checkoutProducts(ProductRequest productRequest) {
            // Set your secret key. Remember to switch to your live secret key in production!
            Stripe.apiKey = secretKey;

            // Create a PaymentIntent with the order amount and currency
            SessionCreateParams.LineItem.PriceData.ProductData productData =
                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                            .setName(productRequest.getName())
                            .build();

            // Create new line item with the above product data and associated price
            SessionCreateParams.LineItem.PriceData priceData =
                    SessionCreateParams.LineItem.PriceData.builder()
                            .setCurrency(productRequest.getCurrency() != null ? productRequest.getCurrency() : "USD")
                            .setUnitAmount(productRequest.getAmount())
                            .setProductData(productData)
                            .build();

            // Create new line item with the above price data
            SessionCreateParams.LineItem lineItem =
                    SessionCreateParams
                            .LineItem.builder()
                            .setQuantity(productRequest.getQuantity())
                            .setPriceData(priceData)
                            .build();

            // Create new session with the line items
            SessionCreateParams params =
                    SessionCreateParams.builder()
                            .setMode(SessionCreateParams.Mode.PAYMENT)
                            .setSuccessUrl("http://localhost:8085/product/v1/success?session_id={CHECKOUT_SESSION_ID}")
                            .setCancelUrl("http://localhost:8085/cancel")
                            .addLineItem(lineItem) 
                            .addExpand("payment_intent") // ✅ Expand payment_intent here
                            .build();
            
            // Create new session
            Session session = null;
            //Session retrievedSession = null;
            try {
                session = Session.create(params);
                
//                retrievedSession = Session.retrieve(
//                        session.getId(),
//                        SessionRetrieveParams.builder()
//                            .addExpand("payment_intent") // Expand payment_intent to fetch its ID
//                            .build()
//                    );
            } catch (StripeException e) {
                //log the error
            	e.printStackTrace();
            }
            
            // ✅ Step 3: Get the payment_intent ID
            String paymentIntentId = session.getPaymentIntent();

            StripeResponse response = new StripeResponse();
            response.setStatus("SUCCESS");
            response.setMessage("Payment session created ");
            response.setSessionId(session.getId());
            response.setSessionUrl(session.getUrl());
            response.setPaymentIntentId(paymentIntentId); // ✅ ADD THIS
            return response;

        }
        
        
        public RefundResponse refundPayment(RefundRequest refundRequest) {
            try {
                Stripe.apiKey = secretKey; // Make sure API key is set

                RefundCreateParams.Builder paramsBuilder = RefundCreateParams.builder()
                        .setPaymentIntent(refundRequest.getPaymentIntentId());

                if (refundRequest.getReason() != null && !refundRequest.getReason().isEmpty()) {
                    paramsBuilder.setReason(RefundCreateParams.Reason.valueOf(refundRequest.getReason().toUpperCase()));
                }

                Refund refund = Refund.create(paramsBuilder.build());

                return new RefundResponse(
                        true,
                        refund.getId(),
                        refund.getStatus(), // ✅ Fetch actual status from Stripe
                        "Refund initiated successfully"
                );

            } catch (StripeException e) {
                return new RefundResponse(
                        false,
                        null,
                        "failed", // You can set a default status
                        "Refund failed: " + e.getMessage()
                );
            }
        }

       

}
