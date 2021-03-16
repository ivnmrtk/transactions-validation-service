package com.github.ivnmrtk.transactionsvalidationservice.exception;

public class KafkaSenderException extends RuntimeException {

    public KafkaSenderException() {
    }

    public KafkaSenderException(String message) {
        super(message);
    }

    public KafkaSenderException(String message, Throwable cause) {
        super(message, cause);
    }

    public KafkaSenderException(Throwable cause) {
        super(cause);

    }
}