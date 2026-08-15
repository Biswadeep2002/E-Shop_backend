package com.example.EmbarkXProject.Service.Analytics;

import com.example.EmbarkXProject.Payload.AnalyticsResponse;
import com.example.EmbarkXProject.Repository.OrderRepository;
import com.example.EmbarkXProject.Repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AnalyticsServiceImpl implements AnalyticsService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    OrderRepository orderRepository;

    @Override
    public AnalyticsResponse getAnalytics() {
        AnalyticsResponse response = new AnalyticsResponse();

        Long productCount = productRepository.count();
        Long totalOrders = orderRepository.count();
        Double totalRevenue = orderRepository.getTotalRevenue() ;

        response.setProductCount(String.valueOf(productCount));
        response.setTotalOrders(String.valueOf(totalOrders));
        response.setTotalRevenue(String.valueOf(totalRevenue != null ? totalRevenue : 0));

        return response;
    }
}
