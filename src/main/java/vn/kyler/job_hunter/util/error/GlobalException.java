package vn.kyler.job_hunter.util.error;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import vn.kyler.job_hunter.domain.response.RestResponse;
import vn.kyler.job_hunter.service.exception.*;

@RestControllerAdvice
public class GlobalException {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RestResponse<Object>> handleAllException(Exception ex) {
        RestResponse<Object> res = new RestResponse<>();
        res.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        res.setMessage(ex.getMessage());
        res.setError("Internal Server Error");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
    }


    @ExceptionHandler(value = AccessDenyException.class)
    public ResponseEntity<RestResponse<Object>> handleAccessDenyException(
            AccessDenyException e) {
        RestResponse<Object> restResponse = new RestResponse<>();
        restResponse.setStatusCode(HttpStatus.FORBIDDEN.value());
        restResponse.setMessage(e.getMessage());
        restResponse.setError("AccessDenyException");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(restResponse);
    }

    @ExceptionHandler(value = NoRefreshTokenInCookieException.class)
    public ResponseEntity<RestResponse<Object>> handleNoRefreshTokenInCookieException(
            NoRefreshTokenInCookieException e) {
        RestResponse<Object> restResponse = new RestResponse<>();
        restResponse.setStatusCode(HttpStatus.UNAUTHORIZED.value());
        restResponse.setMessage(e.getMessage());
        restResponse.setError("NoRefreshTokenInCookieException");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(restResponse);
    }

    @ExceptionHandler(value = MissingRequestCookieException.class)
    public ResponseEntity<RestResponse<Object>> handleMissingRequestCookieException(
            MissingRequestCookieException e) {
        RestResponse<Object> restResponse = new RestResponse<>();
        restResponse.setStatusCode(HttpStatus.UNAUTHORIZED.value());
        restResponse.setMessage(e.getMessage());
        restResponse.setError("MissingRequestCookieException");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(restResponse);
    }

    @ExceptionHandler(value = NotFoundException.class)
    public ResponseEntity<RestResponse<Object>> handleNotFoundException(NotFoundException e) {
        RestResponse<Object> restResponse = new RestResponse<>();
        restResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
        restResponse.setMessage(e.getMessage());
        restResponse.setError("NotFoundException");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(restResponse);
    }

    @ExceptionHandler(value = ExistsException.class)
    public ResponseEntity<RestResponse<Object>> handleEmailExistsException(ExistsException e) {
        RestResponse<Object> restResponse = new RestResponse<>();
        restResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
        restResponse.setMessage(e.getMessage());
        restResponse.setError("EmailExistsException");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(restResponse);
    }

    @ExceptionHandler(value = NoResourceFoundException.class)
    public ResponseEntity<RestResponse<Object>> handleNotFoundException(NoResourceFoundException e) {
        RestResponse<Object> restResponse = new RestResponse<>();
        restResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
        restResponse.setMessage(e.getMessage());
        restResponse.setError("NoResourceFoundException");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(restResponse);
    }

    @ExceptionHandler({UsernameNotFoundException.class,
            BadCredentialsException.class})
    public ResponseEntity<RestResponse<Object>> handleUsernameNotFoundException(Exception e) {
        RestResponse<Object> restResponse = new RestResponse<>();
        restResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
        restResponse.setMessage(e.getMessage());
        restResponse.setError("Exception occurs");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(restResponse);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<RestResponse<Object>> validationError(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();

        RestResponse<Object> restResponse = new RestResponse<>();
        restResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
        restResponse.setError("MethodArgumentNotValidException");
        List<String> errorMessages = fieldErrors.stream().map(fieldError -> fieldError.getDefaultMessage()).toList();
        restResponse.setMessage(errorMessages.size() > 1 ? errorMessages : errorMessages.get(0));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(restResponse);
    }

    @ExceptionHandler(value = StorageException.class)
    public ResponseEntity<RestResponse<Object>> handleFileUploadException(
            StorageException e) {
        RestResponse<Object> restResponse = new RestResponse<>();
        restResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
        restResponse.setMessage(e.getMessage());
        restResponse.setError("FileUploadException");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(restResponse);

    }

    @ExceptionHandler(value = TooManyReqException.class)
    public ResponseEntity<RestResponse<Object>> handleTooManyReqException(
            TooManyReqException e) {
        RestResponse<Object> restResponse = new RestResponse<>();
        restResponse.setStatusCode(HttpStatus.TOO_MANY_REQUESTS.value());
        restResponse.setMessage(e.getMessage());
        restResponse.setError("TooManyReqException");
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(restResponse);

    }
}