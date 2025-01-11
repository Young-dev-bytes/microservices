package com.young.microservices.mlagenteval.exception;

import javax.ws.rs.core.Response;

public class InvocatException extends RuntimeException{
    private static final long serialVersionUID = 8027482777502649656L;
    private Response.StatusType status;
    private Object errorData;

    public InvocatException(Response.StatusType status, Object errorData) {
        this.status = status;
        this.errorData = errorData;
    }

    public InvocatException(Response.StatusType status, String msg) {
        this(status, (Object)(new CommonExceptionData(msg)));
    }

    public InvocatException(int statusCode, String reasonPhrase, Object errorData, Throwable cause) {
        this((Response.StatusType) new HttpStatus(statusCode, reasonPhrase), (Object)errorData, (Throwable)cause);
    }

    public InvocatException(int statusCode, String reasonPhrase, Object errorData) {
        this((Response.StatusType) new HttpStatus(statusCode, reasonPhrase), (Object)errorData);
    }

    public InvocatException(Response.StatusType status, Object errorData, Throwable cause) {
        super(cause);
        this.status = status;
        this.errorData = errorData;
    }

    public InvocatException(Response.StatusType status, String code, String msg) {
        this(status, (Object)(new CommonExceptionData(code, msg)));
    }

    public InvocatException(Response.StatusType status, String code, String msg, Throwable cause) {
        this(status, (Object)(new CommonExceptionData(code, msg)), (Throwable)cause);
    }

    public Response.StatusType getStatus() {
        return this.status;
    }

    public int getStatusCode() {
        return this.status.getStatusCode();
    }

    public String getReasonPhrase() {
        return this.status.getReasonPhrase();
    }

    public Object getErrorData() {
        return this.errorData;
    }

    public <T> T getError() {
        return (T) this.errorData;
    }

    public String getMessage() {
        return this.toString();
    }

    public String toString() {
        return "InvocationException: code=" + this.getStatusCode() + ";msg=" + this.getErrorData();
    }
}
