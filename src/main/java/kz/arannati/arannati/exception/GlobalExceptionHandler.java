package kz.arannati.arannati.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the application.
 * Handles exceptions and returns appropriate responses based on the request type (API or web page).
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final String DEFAULT_ERROR_VIEW = "error/error";
    
    /**
     * Determines if the request is an API request based on the Accept header or URL pattern.
     */
    private boolean isApiRequest(HttpServletRequest request) {
        String accept = request.getHeader("Accept");
        String requestURI = request.getRequestURI();
        
        return (accept != null && accept.contains("application/json")) || 
               requestURI.startsWith("/api/");
    }

    /**
     * Handle ResourceNotFoundException
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public Object handleResourceNotFoundException(ResourceNotFoundException ex, HttpServletRequest request) {
        log.error("Resource not found exception: {}", ex.getMessage());
        
        if (isApiRequest(request)) {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI());
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        } else {
            ModelAndView modelAndView = new ModelAndView(DEFAULT_ERROR_VIEW);
            modelAndView.addObject("errorCode", HttpStatus.NOT_FOUND.value());
            modelAndView.addObject("errorMessage", ex.getMessage());
            modelAndView.addObject("requestUri", request.getRequestURI());
            return modelAndView;
        }
    }

    /**
     * Handle BadRequestException
     */
    @ExceptionHandler(BadRequestException.class)
    public Object handleBadRequestException(BadRequestException ex, HttpServletRequest request) {
        log.error("Bad request exception: {}", ex.getMessage());
        
        if (isApiRequest(request)) {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        } else {
            ModelAndView modelAndView = new ModelAndView(DEFAULT_ERROR_VIEW);
            modelAndView.addObject("errorCode", HttpStatus.BAD_REQUEST.value());
            modelAndView.addObject("errorMessage", ex.getMessage());
            modelAndView.addObject("requestUri", request.getRequestURI());
            return modelAndView;
        }
    }

    /**
     * Handle UnauthorizedException
     */
    @ExceptionHandler(UnauthorizedException.class)
    public Object handleUnauthorizedException(UnauthorizedException ex, HttpServletRequest request) {
        log.error("Unauthorized exception: {}", ex.getMessage());
        
        if (isApiRequest(request)) {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.UNAUTHORIZED, ex.getMessage(), request.getRequestURI());
            return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
        } else {
            return "redirect:/auth/login?error=unauthorized";
        }
    }

    /**
     * Handle ForbiddenException
     */
    @ExceptionHandler(ForbiddenException.class)
    public Object handleForbiddenException(ForbiddenException ex, HttpServletRequest request) {
        log.error("Forbidden exception: {}", ex.getMessage());
        
        if (isApiRequest(request)) {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.FORBIDDEN, ex.getMessage(), request.getRequestURI());
            return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
        } else {
            ModelAndView modelAndView = new ModelAndView(DEFAULT_ERROR_VIEW);
            modelAndView.addObject("errorCode", HttpStatus.FORBIDDEN.value());
            modelAndView.addObject("errorMessage", ex.getMessage());
            modelAndView.addObject("requestUri", request.getRequestURI());
            return modelAndView;
        }
    }

    /**
     * Handle ConflictException
     */
    @ExceptionHandler(ConflictException.class)
    public Object handleConflictException(ConflictException ex, HttpServletRequest request) {
        log.error("Conflict exception: {}", ex.getMessage());
        
        if (isApiRequest(request)) {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.CONFLICT, ex.getMessage(), request.getRequestURI());
            return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
        } else {
            ModelAndView modelAndView = new ModelAndView(DEFAULT_ERROR_VIEW);
            modelAndView.addObject("errorCode", HttpStatus.CONFLICT.value());
            modelAndView.addObject("errorMessage", ex.getMessage());
            modelAndView.addObject("requestUri", request.getRequestURI());
            return modelAndView;
        }
    }

    /**
     * Handle InternalServerException
     */
    @ExceptionHandler(InternalServerException.class)
    public Object handleInternalServerException(InternalServerException ex, HttpServletRequest request) {
        log.error("Internal server exception: {}", ex.getMessage(), ex);
        
        if (isApiRequest(request)) {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request.getRequestURI());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            ModelAndView modelAndView = new ModelAndView(DEFAULT_ERROR_VIEW);
            modelAndView.addObject("errorCode", HttpStatus.INTERNAL_SERVER_ERROR.value());
            modelAndView.addObject("errorMessage", ex.getMessage());
            modelAndView.addObject("requestUri", request.getRequestURI());
            return modelAndView;
        }
    }

    /**
     * Handle Spring Security AuthenticationException
     */
    @ExceptionHandler({AuthenticationException.class, BadCredentialsException.class})
    public Object handleAuthenticationException(Exception ex, HttpServletRequest request) {
        log.error("Authentication exception: {}", ex.getMessage());
        
        if (isApiRequest(request)) {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.UNAUTHORIZED, "Authentication failed: " + ex.getMessage(), request.getRequestURI());
            return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
        } else {
            return "redirect:/auth/login?error=authentication";
        }
    }

    /**
     * Handle Spring Security AccessDeniedException
     */
    @ExceptionHandler(AccessDeniedException.class)
    public Object handleAccessDeniedException(AccessDeniedException ex, HttpServletRequest request) {
        log.error("Access denied exception: {}", ex.getMessage());
        
        if (isApiRequest(request)) {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.FORBIDDEN, "Access denied: " + ex.getMessage(), request.getRequestURI());
            return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
        } else {
            ModelAndView modelAndView = new ModelAndView(DEFAULT_ERROR_VIEW);
            modelAndView.addObject("errorCode", HttpStatus.FORBIDDEN.value());
            modelAndView.addObject("errorMessage", "Access denied: " + ex.getMessage());
            modelAndView.addObject("requestUri", request.getRequestURI());
            return modelAndView;
        }
    }

    /**
     * Handle validation exceptions
     */
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public Object handleValidationExceptions(Exception ex, HttpServletRequest request) {
        log.error("Validation exception: {}", ex.getMessage());
        
        Map<String, String> errors = new HashMap<>();
        if (ex instanceof MethodArgumentNotValidException) {
            ((MethodArgumentNotValidException) ex).getBindingResult().getAllErrors().forEach((error) -> {
                String fieldName = ((FieldError) error).getField();
                String errorMessage = error.getDefaultMessage();
                errors.put(fieldName, errorMessage);
            });
        } else if (ex instanceof BindException) {
            ((BindException) ex).getBindingResult().getAllErrors().forEach((error) -> {
                String fieldName = ((FieldError) error).getField();
                String errorMessage = error.getDefaultMessage();
                errors.put(fieldName, errorMessage);
            });
        }
        
        if (isApiRequest(request)) {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.BAD_REQUEST, "Validation failed", request.getRequestURI());
            
            errors.forEach(errorResponse::addValidationError);
            
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        } else {
            ModelAndView modelAndView = new ModelAndView(DEFAULT_ERROR_VIEW);
            modelAndView.addObject("errorCode", HttpStatus.BAD_REQUEST.value());
            modelAndView.addObject("errorMessage", "Validation failed");
            modelAndView.addObject("errors", errors);
            modelAndView.addObject("requestUri", request.getRequestURI());
            return modelAndView;
        }
    }

    /**
     * Handle MethodArgumentTypeMismatchException
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public Object handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        log.error("Method argument type mismatch: {}", ex.getMessage());
        
        String error = String.format("Parameter '%s' should be of type '%s'", 
                ex.getName(), ex.getRequiredType().getSimpleName());
        
        if (isApiRequest(request)) {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.BAD_REQUEST, error, request.getRequestURI());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        } else {
            ModelAndView modelAndView = new ModelAndView(DEFAULT_ERROR_VIEW);
            modelAndView.addObject("errorCode", HttpStatus.BAD_REQUEST.value());
            modelAndView.addObject("errorMessage", error);
            modelAndView.addObject("requestUri", request.getRequestURI());
            return modelAndView;
        }
    }

    /**
     * Handle MaxUploadSizeExceededException
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public Object handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex, HttpServletRequest request) {
        log.error("Max upload size exceeded: {}", ex.getMessage());
        
        String error = "File size exceeds the maximum allowed limit";
        
        if (isApiRequest(request)) {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.BAD_REQUEST, error, request.getRequestURI());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        } else {
            ModelAndView modelAndView = new ModelAndView(DEFAULT_ERROR_VIEW);
            modelAndView.addObject("errorCode", HttpStatus.BAD_REQUEST.value());
            modelAndView.addObject("errorMessage", error);
            modelAndView.addObject("requestUri", request.getRequestURI());
            return modelAndView;
        }
    }

    /**
     * Handle NoHandlerFoundException
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public Object handleNoHandlerFoundException(NoHandlerFoundException ex, HttpServletRequest request) {
        log.error("No handler found: {}", ex.getMessage());
        
        String error = String.format("No handler found for %s %s", ex.getHttpMethod(), ex.getRequestURL());
        
        if (isApiRequest(request)) {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.NOT_FOUND, error, request.getRequestURI());
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        } else {
            ModelAndView modelAndView = new ModelAndView(DEFAULT_ERROR_VIEW);
            modelAndView.addObject("errorCode", HttpStatus.NOT_FOUND.value());
            modelAndView.addObject("errorMessage", error);
            modelAndView.addObject("requestUri", request.getRequestURI());
            return modelAndView;
        }
    }

    /**
     * Handle all other exceptions
     */
    @ExceptionHandler(Exception.class)
    public Object handleAllExceptions(Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception: {}", ex.getMessage(), ex);
        
        if (isApiRequest(request)) {
            ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", request.getRequestURI());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            ModelAndView modelAndView = new ModelAndView(DEFAULT_ERROR_VIEW);
            modelAndView.addObject("errorCode", HttpStatus.INTERNAL_SERVER_ERROR.value());
            modelAndView.addObject("errorMessage", "An unexpected error occurred");
            modelAndView.addObject("requestUri", request.getRequestURI());
            return modelAndView;
        }
    }
}