package com.xy.apple.exception;

public class ParamNotFoundException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ParamNotFoundException() {
        super();
    }

    public ParamNotFoundException(String s) {
        super(s);
    }

    public ParamNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParamNotFoundException(Throwable cause) {
        super(cause);
    }
}
