package com.example.EmbarkXProject.Payload.Response;

import lombok.Data;

@Data

public class MessageResponse {

    private String message;

    public MessageResponse(String response) {
        this.message = response;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
