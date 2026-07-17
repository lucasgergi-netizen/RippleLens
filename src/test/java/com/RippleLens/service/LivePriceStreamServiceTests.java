package com.RippleLens.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.RippleLens.security.RateLimitExceededException;

class LivePriceStreamServiceTests {

    @Test
    void limitsConcurrentStreamsPerClient() {
        LivePriceProviderService provider = mock(LivePriceProviderService.class);
        when(provider.getCurrentSnapshot()).thenReturn(List.of());
        LivePriceStreamService service = new LivePriceStreamService(provider);

        service.createEmitter("client-one");
        service.createEmitter("client-one");
        service.createEmitter("client-one");

        assertThatThrownBy(() -> service.createEmitter("client-one"))
                .isInstanceOf(RateLimitExceededException.class);
        service.createEmitter("client-two");
    }
}
