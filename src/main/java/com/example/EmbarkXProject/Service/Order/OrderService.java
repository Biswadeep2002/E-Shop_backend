package com.example.EmbarkXProject.Service.Order;

import com.example.EmbarkXProject.Payload.Order.OrderDTO;
import com.example.EmbarkXProject.Payload.OrderResponse;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Transactional
public interface OrderService {
    OrderDTO placeOrder(String emailId, long addressId, String paymentMethod, String pgName, String pgPaymentId, String pgStatus, String pgResponseMessage);

    OrderResponse getAllOrders(int pageNumber, int pageSize, String sortBy, String sortOrder);

    OrderDTO updateOrder(Long orderId, String status);

    OrderResponse getAllSellerOrders(int pageNumber, int pageSize, String sortBy, String sortOrder);

    OrderResponse getMyOrders();

}
