package com.audit.client;

public class AuditLoggerException extends RuntimeException {
	public AuditLoggerException() {
		super();
	}

	public AuditLoggerException(String message) {
		super(message);
	}

	public AuditLoggerException(String message, Throwable cause) {
		super(message, cause);
	}

	@Override
	public String toString() {
		return super.toString();
	}

	@Override
	public String getMessage() {
		return super.getMessage();
	}

	public void setMessage(String message) {
	}
}
