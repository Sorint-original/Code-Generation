package com.bankapp.Backend.DTO;

public class DashboardStatusResponse {


    private final String message;

    public DashboardStatusResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
