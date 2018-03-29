package com.xy.apple.exception;

public class RpcException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RpcException() {
        super();
    }

    public RpcException(String s) {
        super(s);
    }

    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }

    public RpcException(Throwable cause) {
        super(cause);
    }
}
