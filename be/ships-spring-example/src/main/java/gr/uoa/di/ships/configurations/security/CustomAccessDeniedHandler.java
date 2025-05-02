package gr.uoa.di.ships.configurations.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)
      throws IOException, ServletException {
    System.out.println("Prohibited access attempt to: " + request.getRequestURI());
    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
  }
}
