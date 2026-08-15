package com.example.EmbarkXProject.Service.Stripe;

import com.example.EmbarkXProject.Exceptions.exceptions.APIException;
import com.example.EmbarkXProject.Payload.StripePaymentDto;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.CustomerSearchResult;
import com.stripe.model.PaymentIntent;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.CustomerSearchParams;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.swing.plaf.IconUIResource;

@Service
@Transactional
public class StripeServiceImpl implements StripeService {

    @Value("${stripe.secret.key}")
    private String stripeApiKey;

    @PostConstruct
    public void init(){
        System.out.println("Stripe Key Loaded: " + stripeApiKey);
        Stripe.apiKey=stripeApiKey;
    }

    @Override
    public PaymentIntent paymentIntent(StripePaymentDto stripePaymentDto) throws StripeException {

        if (stripePaymentDto.getAddress() == null) {
            throw new APIException("Address is required");
        }

        if (stripePaymentDto.getEmail() == null || stripePaymentDto.getEmail().isBlank()) {
            throw new APIException("Email is required");
        }

        if (stripePaymentDto.getName() == null || stripePaymentDto.getName().isBlank()) {
            throw new APIException("Name is required");
        }

        Customer customer;

        CustomerSearchParams searchParams = CustomerSearchParams.builder()
                .setQuery("email:'" + stripePaymentDto.getEmail() + "'")
                .build();

        CustomerSearchResult customers = Customer.search(searchParams);
        if(customers.getData().isEmpty()){
            CustomerCreateParams customerParams = CustomerCreateParams.builder()
                                            .setName(stripePaymentDto.getName())
                                            .setEmail(stripePaymentDto.getEmail())
                                            .setAddress(
                                                    CustomerCreateParams.Address.builder()
                                                            .setLine1(stripePaymentDto.getAddress().getStreet())
                                                            .setCity(stripePaymentDto.getAddress().getCity())
                                                            .setState(stripePaymentDto.getAddress().getState())
                                                            .setPostalCode(stripePaymentDto.getAddress().getPincode())
                                                            .setCountry(stripePaymentDto.getAddress().getCountry())
                                                            .build()
                                            )
                                            .build();
            customer = Customer.create(customerParams);
        }else {
            customer = customers.getData().get(0);
        }

        PaymentIntentCreateParams params =
                PaymentIntentCreateParams.builder()
                        .setAmount(stripePaymentDto.getAmount())
                        .setCurrency(stripePaymentDto.getCurrency())
                        .setCustomer(customer.getId())
                        .setDescription(stripePaymentDto.getDescription())
                        .setAutomaticPaymentMethods(
                                PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                        .setEnabled(true)
                                        .build()
                        )
                        .build();

        return PaymentIntent.create(params);
    }
}


