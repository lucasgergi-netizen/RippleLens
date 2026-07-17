package com.RippleLens.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class PasswordResetEventListener {

    private final PasswordResetDeliveryService deliveryService;

    public PasswordResetEventListener(PasswordResetDeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void deliver(PasswordResetRequestedEvent event) {
        deliveryService.sendResetLink(event.email(), event.resetLink());
    }
}
