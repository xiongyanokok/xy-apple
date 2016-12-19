package com.xy.apple.common.bean;

/**
 * 封装 RPC 响应
 *
 * @author xiongyan
 * @since 1.0.0
 */
public class RpcResponse {

	/**
	 * 请求唯一码
	 */
    private String requestId;
    
    /**
     * 异常信息
     */
    private Exception exception;
    
    /**
     * 响应结果
     */
    private Object result;

    public boolean hasException() {
        return exception != null;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Throwable getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
