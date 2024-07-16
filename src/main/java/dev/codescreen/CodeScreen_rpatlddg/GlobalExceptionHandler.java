package dev.codescreen.CodeScreen_rpatlddg;

import dev.codescreen.CodeScreen_rpatlddg.dto.Error;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Error> handleInternalServerError(Exception ex) {
        Error error = new Error("500 INTERNAL_SERVER_ERROR","An internal server error occurred: " + ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Error> handleException(Exception ex) {
        Error error = new Error("DATA_ERROR(Check request again and retry)", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler({ MethodArgumentNotValidException.class, IllegalArgumentException.class, MissingServletRequestParameterException.class })
    public ResponseEntity<Error> handleBadRequest(Exception ex) {
        Error error = new Error("400 BAD_REQUEST (Check request again and retry)",ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }



}
