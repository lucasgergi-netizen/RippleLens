package com.RippleLens.service;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(
        name = "ripplelens.security.password-reset.delivery-enabled",
        havingValue = "false",
        matchIfMissing = true)
public class NoOpPasswordResetDeliveryService implements PasswordResetDeliveryService {

    @Override
    public void sendResetLink(String email, String resetLink) {
        // Recovery stays safely disabled until an explicit delivery provider is configured.
    }
}
