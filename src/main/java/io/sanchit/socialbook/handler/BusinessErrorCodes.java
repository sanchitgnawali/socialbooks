package io.sanchit.socialbook.handler;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public enum BusinessErrorCodes {

    NO_CODE(0, "No code", HttpStatus.NOT_IMPLEMENTED),
    ACCOUNT_LOCKED(302, "User Account Is Locked.", HttpStatus.FORBIDDEN),
    INCORRECT_CURRENT_PASSWORD(300, "Current password is incorrect.", HttpStatus.BAD_REQUEST),
    NEW_PASSWORD_DOES_NOT_MATCH(301, "New password do not match.", HttpStatus.BAD_REQUEST),
    ACCOUNT_DISABLED(303, "Account is disabled.", HttpStatus.FORBIDDEN),
    BAD_CREDENTIALS(304, "Email or password invalid.", HttpStatus.FORBIDDEN);

    @Getter
    private final int code;

    @Getter
    private final String description;

    @Getter
    private final HttpStatus httpStatus;

    BusinessErrorCodes(int code, String description, HttpStatus httpStatus) {
        this.code = code;
        this.description = description;
        this.httpStatus = httpStatus;
    }

    ;
}
