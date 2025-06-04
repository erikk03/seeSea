package gr.uoa.di.ships.configurations.exceptions.handler;

import gr.uoa.di.ships.configurations.exceptions.InvalidCredentialsException;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler {

  @ExceptionHandler(InvalidCredentialsException.class)
  public ResponseEntity<Map<String, String>> handleInvalidCredentials(InvalidCredentialsException ex) {
    Map<String, String> body = Map.of("error", ex.getMessage());
    return ResponseEntity
        .status(HttpStatus.UNAUTHORIZED)
        .body(body);
  }

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<Map<String, String>> handleRuntime(RuntimeException ex) {
    Map<String, String> body = Map.of("error", ex.getMessage());
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(body);
  }
}