package com.RippleLens.security;

import java.io.IOException;

public class RequestSizeExceededException extends IOException {

    public RequestSizeExceededException() {
        super("Request body exceeded the configured limit.");
    }
}
