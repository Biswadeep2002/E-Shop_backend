package com.example.EmbarkXProject.Service.Order;

import com.example.EmbarkXProject.Exceptions.exceptions.APIException;
import com.example.EmbarkXProject.Exceptions.exceptions.ResourceNotFoundException;
import com.example.EmbarkXProject.Model.*;
import com.example.EmbarkXProject.Payload.Order.OrderDTO;
import com.example.EmbarkXProject.Payload.Order.OrderItemDTO;
import com.example.EmbarkXProject.Payload.OrderResponse;
import com.example.EmbarkXProject.Repository.*;
import com.example.EmbarkXProject.Service.Cart.CartService;
import com.example.EmbarkXProject.Utill.AuthUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    AuthUtil authUtil;

    @Autowired
    CartRepository cartRepository;

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderItemsRepository orderItemsRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    CartService cartService;

    @Override
    public OrderDTO placeOrder(String emailId, long addressId, String paymentMethod, String pgName, String pgPaymentId, String pgStatus, String pgResponseMessage) {

        //Getting user cart

        Cart cart = cartRepository.findCartByEmail(emailId);
        if(cart == null)
            throw new ResourceNotFoundException("Cart", "Email-Id", emailId);

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "Address Id", addressId));

        //Create a new order with payment info

        Order order = new Order();
        order.setEmail(emailId);
        order.setOrderDate(LocalDate.now());
        order.setTotalAmount(cart.getTotalPrice());
        order.setOrderStatus("Accepted");
        order.setAddress(address);

        Payment payment = new Payment(paymentMethod, pgPaymentId, pgStatus, pgResponseMessage, pgName);
        payment.setOrder(order);
        payment = paymentRepository.save(payment);
        order.setPayment(payment);

        Order savedOrder = orderRepository.save(order);

        //Get items from the cart into the order items

        List<CartItem> cartItems = cart.getCartItems();
        if(cartItems.isEmpty())
            throw new APIException("Cart is empty");

        List<OrderItems> orderItems = new ArrayList<>();
        for(CartItem cartItem : cartItems){
            OrderItems orderItem = new OrderItems();
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setOrderedProductPrize(cartItem.getProductPrize());
            orderItem.setOrder(savedOrder);
            orderItems.add(orderItem);
        }

        orderItems = orderItemsRepository.saveAll(orderItems);

        //Update product stock

        cart.getCartItems().forEach(item -> {
            int quantity = item.getQuantity();
            Product product = item.getProduct();
            product.setQuantity(product.getQuantity() - quantity);
            productRepository.save(product);

            //Clear the cart

            cartService.deleteProductFromCart(cart.getCartId(), item.getProduct().getProductId());
        });

            //Send back the order summary

            OrderDTO orderDTO = modelMapper.map(savedOrder, OrderDTO.class);
            orderItems.forEach(i ->orderDTO.getOrderItems().add(modelMapper.map(i, OrderItemDTO.class)));

        orderDTO.setAddressId(addressId);
        return orderDTO;
    }

    @Override
    public OrderResponse getAllOrders(int pageNumber, int pageSize, String sortBy, String sortOrder) {

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                                ? Sort.by(sortBy).ascending()
                                : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Order> pageOrders = orderRepository.findAll(pageDetails);
        List<Order> orders = pageOrders.getContent();
        List<OrderDTO> orderDTOS = orders.stream()
                .map(order -> modelMapper.map(order, OrderDTO.class)).toList();

        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setContent(orderDTOS);
        orderResponse.setPageNumber(pageOrders.getNumber());
        orderResponse.setPageSize(pageOrders.getSize());
        orderResponse.setTotalPages(pageOrders.getTotalPages());
        orderResponse.setTotalElements(pageOrders.getTotalElements());
        orderResponse.setLastPage(pageOrders.isLast());

        return orderResponse;
    }

    @Override
    public OrderDTO updateOrder(Long orderId, String status) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order", "Order Id", orderId));
        order.setOrderStatus(status);
        orderRepository.save(order);
        return modelMapper.map(order, OrderDTO.class);
    }

    @Override
    public OrderResponse getAllSellerOrders(int pageNumber, int pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);


        Users seller = authUtil.getLoggedInUser();

        Page<Order> pageOrders = orderRepository.findAll(pageDetails);

        List<Order> sellerOrders = pageOrders.getContent().stream().
                        filter(order -> order.getOrderItems().stream()
                                .anyMatch(orderItem -> {
                                    var product = orderItem.getProduct();
                                    if (product == null || product.getUser() == null)
                                        return false;
                                    return product.getUser().getUserId().equals(seller.getUserId());
                                })).toList();


        List<OrderDTO> orderDTOS = sellerOrders.stream()
                .map(order -> modelMapper.map(order, OrderDTO.class)).toList();

        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setContent(orderDTOS);
        orderResponse.setPageNumber(pageOrders.getNumber());
        orderResponse.setPageSize(pageOrders.getSize());


        orderResponse.setTotalPages(pageOrders.getTotalPages());
        orderResponse.setTotalElements(pageOrders.getTotalElements());
        orderResponse.setLastPage(pageOrders.isLast());

        return orderResponse;
    }



    @Override
    public OrderResponse getMyOrders() {

        String userEmail = authUtil.getLoggedInUserEmail();

        List<Order> orders = orderRepository.findByEmail(userEmail);
        List<OrderDTO> orderDTOS = orders.stream()
                .map(order -> modelMapper.map(order, OrderDTO.class)).toList();

        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setContent(orderDTOS);
        orderResponse.setPageNumber(0);
        orderResponse.setPageSize(orderDTOS.size());
        orderResponse.setTotalPages(1);
        orderResponse.setTotalElements((long) orderDTOS.size());
        System.out.println("Total orders are : " + orderResponse.getTotalElements());
        orderResponse.setLastPage(true);
        return orderResponse;
    }
}


