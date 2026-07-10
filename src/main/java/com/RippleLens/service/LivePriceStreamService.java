package com.RippleLens.service;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.RippleLens.live.LivePriceTickResponse;

@Service
public class LivePriceStreamService {

    private final LivePriceProviderService livePriceProviderService;
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    public LivePriceStreamService(LivePriceProviderService livePriceProviderService) {
        this.livePriceProviderService = livePriceProviderService;
    }

    public SseEmitter createEmitter() {
        SseEmitter emitter = new SseEmitter(0L);
        emitters.add(emitter);
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError(error -> emitters.remove(emitter));

        try {
            emitter.send(SseEmitter.event()
                    .name("connected")
                    .data("RippleLens real provider stream connected"));
            emitter.send(SseEmitter.event()
                    .name("price-batch")
                    .data(livePriceProviderService.getCurrentSnapshot()));
        } catch (IOException error) {
            emitters.remove(emitter);
            emitter.completeWithError(error);
        }
        return emitter;
    }

    @Scheduled(
            fixedDelayString = "${ripplelens.live.tick-ms:3000}",
            initialDelayString = "${ripplelens.live.initial-delay-ms:1500}")
    public void publishTicks() {
        if (emitters.isEmpty()) {
            return;
        }

        List<LivePriceTickResponse> ticks = livePriceProviderService.getRealPriceBatch();
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("price-batch")
                        .data(ticks));
            } catch (IOException | IllegalStateException error) {
                emitters.remove(emitter);
                emitter.completeWithError(error);
            }
        }
    }
}
