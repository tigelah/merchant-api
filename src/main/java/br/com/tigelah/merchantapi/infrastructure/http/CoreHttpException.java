package br.com.tigelah.merchantapi.infrastructure.http;

public class CoreHttpException extends RuntimeException {
    private final int statusCode;
    private final String body;

    public CoreHttpException(int statusCode, String body) {
        super("acquirer-core error: " + statusCode);
        this.statusCode = statusCode;
        this.body = body;
    }

    public int statusCode() { return statusCode; }
    public String body() { return body; }
}
