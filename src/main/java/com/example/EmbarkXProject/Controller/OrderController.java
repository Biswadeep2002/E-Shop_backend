package com.example.EmbarkXProject.Controller;

import com.example.EmbarkXProject.Config.AppConstants;
import com.example.EmbarkXProject.Payload.Order.OrderDTO;
import com.example.EmbarkXProject.Payload.Order.OrderRequestDTO;
import com.example.EmbarkXProject.Payload.OrderResponse;
import com.example.EmbarkXProject.Payload.OrderStatusUpdateDTO;
import com.example.EmbarkXProject.Payload.StripePaymentDto;
import com.example.EmbarkXProject.Service.Order.OrderService;
import com.example.EmbarkXProject.Service.Stripe.StripeService;
import com.example.EmbarkXProject.Utill.AuthUtil;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
 import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/order")
public class OrderController {

    @Autowired
    AuthUtil authUtil;

    @Autowired
    OrderService orderService;

    @Autowired
    StripeService stripeService;

    @PostMapping("/users/payments/{paymentMethod}")
    public ResponseEntity<OrderDTO> placeOrder(@PathVariable String paymentMethod,
                                               @RequestBody OrderRequestDTO orderRequestDTO){

        String emailId = authUtil.getLoggedInUserEmail();
        System.out.println("OrderRequestDTO DATA: " + orderRequestDTO);
        OrderDTO order = orderService.placeOrder(
                emailId,
                orderRequestDTO.getAddressId(),
                paymentMethod,
                orderRequestDTO.getPgName(),
                orderRequestDTO.getPgPaymentId(),
                orderRequestDTO.getPgStatus(),
                orderRequestDTO.getPgResponseMessage());

        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }

    @GetMapping("/admin/orders")
    public ResponseEntity<OrderResponse> getAllOrders(@RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) int pageNumber,
                                                      @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) int pageSize,
                                                      @RequestParam(name = "sortByProductId", defaultValue = AppConstants.ORDERS_SORT_BY, required = false) String sortBy,
                                                      @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_ORDER, required = false) String sortOrder
    ){
        OrderResponse orderResponse = orderService.getAllOrders(pageNumber, pageSize, sortBy, sortOrder);
        return new ResponseEntity<OrderResponse>(orderResponse, HttpStatus.OK);
    }

    @GetMapping("/profile/order")
    public ResponseEntity<OrderResponse> getMyOrders(){
        OrderResponse orderResponse = orderService.getMyOrders();
        return new ResponseEntity<OrderResponse>(orderResponse, HttpStatus.OK);
    }


    @GetMapping("/seller/orders")
    public ResponseEntity<OrderResponse> getAllSellerOrders(@RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) int pageNumber,
                                                      @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) int pageSize,
                                                      @RequestParam(name = "sortByProductId", defaultValue = AppConstants.ORDERS_SORT_BY, required = false) String sortBy,
                                                      @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_ORDER, required = false) String sortOrder
    ){
        OrderResponse orderResponse = orderService.getAllSellerOrders(pageNumber, pageSize, sortBy, sortOrder);
        return new ResponseEntity<OrderResponse>(orderResponse, HttpStatus.OK);
    }

    @PostMapping("/stripe-client-secret")
    public ResponseEntity<String> createStripeClientSecret(@RequestBody StripePaymentDto stripePaymentDto) throws StripeException {

        System.out.println("StripePaymentDTO received" + stripePaymentDto );

        System.out.println("Amount received = " + stripePaymentDto.getAmount());
        System.out.println("Currency received = " + stripePaymentDto.getCurrency());

        PaymentIntent paymentIntent = stripeService.paymentIntent(stripePaymentDto);


        System.out.println("PaymentIntent amount = " + paymentIntent.getAmount());
        System.out.println("PaymentIntent client secret = " + paymentIntent.getClientSecret());
        return new ResponseEntity<>(paymentIntent.getClientSecret(), HttpStatus.CREATED);
    }

    @PutMapping("/admin/orders/{orderId}/status")
    public ResponseEntity<OrderDTO> updateOrderStatus(@PathVariable Long orderId, @RequestBody OrderStatusUpdateDTO orderStatusUpdateDTO){

        OrderDTO orderDTO = orderService.updateOrder(orderId, orderStatusUpdateDTO.getStatus());
        return new ResponseEntity<OrderDTO>(orderDTO, HttpStatus.OK);
    }

    @PutMapping("/seller/orders/{orderId}/status")
    public ResponseEntity<OrderDTO> updateOrderStatusSeller(@PathVariable Long orderId, @RequestBody OrderStatusUpdateDTO orderStatusUpdateDTO){

        OrderDTO orderDTO = orderService.updateOrder(orderId, orderStatusUpdateDTO.getStatus());
        return new ResponseEntity<OrderDTO>(orderDTO, HttpStatus.OK);
    }
    }
