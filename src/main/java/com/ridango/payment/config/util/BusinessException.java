package com.ridango.payment.config.util;

import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class BusinessException extends ResponseStatusException {

    public BusinessException(BusinessError businessError) {
        this(businessError.getErrorCode() + ": " + businessError.getErrorMessage());
    }

    public BusinessException(String message) {
        super(BAD_REQUEST, message);
    }


}
