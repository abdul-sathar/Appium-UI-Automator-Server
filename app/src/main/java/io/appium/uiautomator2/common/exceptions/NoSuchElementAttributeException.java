package io.appium.uiautomator2.common.exceptions;

public class NoSuchElementAttributeException extends RuntimeException {
    private static final long serialVersionUID = -4526232656079801503L;

    public NoSuchElementAttributeException(String message) {
        super(message);
    }

    public NoSuchElementAttributeException(Throwable t) {
        super(t);
    }

    public NoSuchElementAttributeException(String message, Throwable t) {
        super(message, t);
    }
}
