package com.RippleLens.service;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.RippleLens.live.LivePriceTickResponse;
import com.RippleLens.security.RateLimitExceededException;

@Service
public class LivePriceStreamService {

    private static final int MAX_STREAMS_PER_CLIENT = 3;
    private static final int MAX_STREAMS_GLOBAL = 500;
    private static final long STREAM_TIMEOUT_MILLIS = Duration.ofMinutes(5).toMillis();

    private final LivePriceProviderService livePriceProviderService;
    private final List<ClientEmitter> emitters = new CopyOnWriteArrayList<>();

    public LivePriceStreamService(LivePriceProviderService livePriceProviderService) {
        this.livePriceProviderService = livePriceProviderService;
    }

    public synchronized SseEmitter createEmitter(String clientKey) {
        long clientStreams = emitters.stream().filter(client -> client.clientKey().equals(clientKey)).count();
        if (clientStreams >= MAX_STREAMS_PER_CLIENT || emitters.size() >= MAX_STREAMS_GLOBAL) {
            throw new RateLimitExceededException(60);
        }

        SseEmitter emitter = new SseEmitter(STREAM_TIMEOUT_MILLIS);
        ClientEmitter client = new ClientEmitter(clientKey, emitter);
        emitters.add(client);
        emitter.onCompletion(() -> remove(client));
        emitter.onTimeout(() -> remove(client));
        emitter.onError(error -> remove(client));

        try {
            emitter.send(SseEmitter.event()
                    .name("connected")
                    .data("RippleLens real provider stream connected"));
            emitter.send(SseEmitter.event()
                    .name("price-batch")
                    .data(livePriceProviderService.getCurrentSnapshot()));
        } catch (IOException error) {
            remove(client);
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
        for (ClientEmitter client : emitters) {
            try {
                client.emitter().send(SseEmitter.event()
                        .name("price-batch")
                        .data(ticks));
            } catch (IOException | IllegalStateException error) {
                remove(client);
                client.emitter().completeWithError(error);
            }
        }
    }

    private synchronized void remove(ClientEmitter client) {
        emitters.remove(client);
    }

    private record ClientEmitter(String clientKey, SseEmitter emitter) {
    }
}
