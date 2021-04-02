package com.katouyi.tools.system.core;

public class ServiceException extends Exception {
    private static final long serialVersionUID = -8559448572367908328L;
    private ErrorCode errorCode;
    private int code;

    public ServiceException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return this.errorCode;
    }

    public int getCode() {
        return this.code;
    }

    public ServiceException(int errorCode, String message) {
        super(message);
        this.code = errorCode;
    }

    public ServiceException(ErrorCode errorCode, Throwable clauses) {
        super(errorCode.getMessage(), clauses);
    }

    public String getMessage() {
        return this.errorCode != null ? this.errorCode.getMessage() : super.getMessage();
    }
}
