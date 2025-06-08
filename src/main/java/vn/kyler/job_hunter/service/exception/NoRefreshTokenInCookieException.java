package vn.kyler.job_hunter.service.exception;

public class NoRefreshTokenInCookieException extends Exception {
    public NoRefreshTokenInCookieException(String message) {
        super(message);
    }
}
