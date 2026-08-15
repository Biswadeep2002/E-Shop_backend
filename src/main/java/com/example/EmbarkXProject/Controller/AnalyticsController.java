package com.example.EmbarkXProject.Controller;

import com.example.EmbarkXProject.Payload.AnalyticsResponse;
import com.example.EmbarkXProject.Service.Analytics.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    @GetMapping("admin/analytics")
    public ResponseEntity<AnalyticsResponse> getAnalytics(){
        AnalyticsResponse analyticsResponse = analyticsService.getAnalytics();
        return new ResponseEntity<AnalyticsResponse>(analyticsResponse, HttpStatus.OK);
    }
}
