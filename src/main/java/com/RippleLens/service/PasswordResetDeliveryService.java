package com.RippleLens.service;

public interface PasswordResetDeliveryService {

    void sendResetLink(String email, String resetLink);
}
