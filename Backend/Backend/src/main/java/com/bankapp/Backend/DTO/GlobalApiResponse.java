package com.bankapp.Backend.DTO;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Standard API response wrapper")
public class GlobalApiResponse{

    @Schema(description = "Whether the request was successful", example = "true")
    private boolean success;

    @Schema(description = "Descriptive message", example = "Customer approved successfully")
    private String message;



    // Constructors
    public GlobalApiResponse() {}

    public GlobalApiResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    // Getters & setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}

