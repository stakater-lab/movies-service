package com.stakater.lab.moviesservice.rest;

import com.stakater.lab.moviesservice.security.ApiError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.rest.core.RepositoryConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.util.WebUtils;

import java.util.List;

/**
 * Controller advice to translate the server side exceptions to client-friendly json structures.
 *
 * Component to implement global exception handling and customize the response based on the exception type.
 */

/**
 * ExceptionHandler is a Spring annotation that provides a mechanism to treat exceptions that are thrown during execution
 * of handlers (Controller operations). This annotation, if used on methods of controller classes, will serve as the
 * entry point for handling exceptions thrown within this controller only. Altogether, the most common way is to use
 * @ExceptionHandler on methods of @ControllerAdvice classes so that the exception handling will be applied
 * globally or to a subset of controllers.
 */

/**
 * ControllerAdvice is an annotation introduced in Spring 3.2, and as the name suggests, is “Advice” for multiple
 * controllers. It is used to enable a single ExceptionHandler to be applied to multiple controllers. This way we can in
 * just one place define how to treat such an exception and this handler will be called when the exception is thrown from
 * classes that are covered by this ControllerAdvice. The subset of controllers affected can defined by using the
 * following selectors on @ControllerAdvice: annotations(), basePackageClasses(), and basePackages(). If no selectors are
 * provided, then the ControllerAdvice is applied globally to all controllers.
 *
 * So by using @ExceptionHandler and @ControllerAdvice, we’ll be able to define a central point for treating exceptions
 * and wrapping them up in an ApiError object with better organization than the default Spring Boot error handling mechanism.
 */
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestExceptionHandler.class);

    /**
     * A single place to customize the response body of all Exception types.
     *
     * <p>The default implementation sets the {@link WebUtils#ERROR_EXCEPTION_ATTRIBUTE}
     * request attribute and creates a {@link ResponseEntity} from the given
     * body, headers, and status.
     *
     * @param ex the exception
     * @param body the body for the response
     * @param headers the headers for the response
     * @param status the response status
     * @param request the current request
     */
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
                                                             HttpStatus status, WebRequest request) {
        LOGGER.error(String.format("Exception - error message : %s", ex.getMessage()), ex);

        if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status)) {
            request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, ex, WebRequest.SCOPE_REQUEST);
        }

        if(body == null)    // all default handlers have null body so, i build response body myself
        {
            body = ApiError
                    .newBuilder()
                    .status(status.toString())
                    .detail(ex.getMessage())
                    .build();
        }

        return new ResponseEntity<>(body, headers, status);
    }

    /**
     * Customize the response for MethodArgumentNotValidException.
     * <p>This method delegates to {@link #handleExceptionInternal}.
     *
     * @param ex the exception
     * @param headers the headers to be written to the response
     * @param status the selected response status
     * @param request the current request
     *
     * @return a {@code ResponseEntity} instance
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatus status, WebRequest request) {
        return handleExceptionInternal(ex, processFieldErrors(ex.getBindingResult().getFieldErrors()), headers, status, request);
    }

    @ExceptionHandler(RepositoryConstraintViolationException.class)
    public ResponseEntity<Object> handleRepositoryConstraintViolationException(Exception ex, WebRequest request) {
        HttpHeaders headers = new HttpHeaders();
        RepositoryConstraintViolationException nevEx = (RepositoryConstraintViolationException) ex;

        return handleExceptionInternal(ex, processFieldErrors(nevEx.getErrors().getFieldErrors()), headers, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        HttpHeaders headers = new HttpHeaders();
        ApiError apiError = ApiError
                .newBuilder()
                .status(HttpStatus.BAD_REQUEST.toString())
                .build();

        return handleExceptionInternal(ex, apiError, headers, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        HttpHeaders headers = new HttpHeaders();

        ApiError apiError = ApiError
                .newBuilder()
                .status(HttpStatus.FORBIDDEN.toString())
                .build();

        return handleExceptionInternal(ex, apiError, headers, HttpStatus.FORBIDDEN, request);
    }

    /**
     * Catch every other exception and ensure JSON response is sent back!
     * <p>This method delegates to {@link #handleExceptionInternal}.
     *
     * @param ex the exception
     * @param request the current request
     *
     * @return a {@code ResponseEntity} instance
     */
    @ExceptionHandler(Throwable.class)
    public ResponseEntity<Object> handleThrowableException(Throwable ex, WebRequest request) {
        LOGGER.error(String.format("Throwable - error message : %s", ex.getMessage()), ex);

        HttpHeaders headers = new HttpHeaders();
        ApiError apiError =  ApiError.newBuilder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.toString())
                .build();

        return handleExceptionInternal(new Exception(ex), apiError, headers, HttpStatus.BAD_REQUEST, request);
    }

    /**
     * Build API error response from field errors
     *
     * @param fieldErrors list of field errors
     *
     * @return a {@code ApiError} instance
     */
    private ApiError processFieldErrors(List<FieldError> fieldErrors) {
        ApiError apiError = ApiError.newBuilder().status(HttpStatus.BAD_REQUEST.toString()).build();

        for (FieldError fieldError : fieldErrors) {
            apiError.addFieldError(fieldError.getObjectName(), fieldError.getField(), fieldError.getDefaultMessage());
        }

        return apiError;
    }
}
