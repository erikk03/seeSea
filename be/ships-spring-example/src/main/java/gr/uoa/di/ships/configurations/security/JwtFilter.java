package gr.uoa.di.ships.configurations.security;

import gr.uoa.di.ships.services.implementation.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtFilter extends OncePerRequestFilter {

  private final JwtService jwtService;
  private final ApplicationContext context;

  public JwtFilter(JwtService jwtService, ApplicationContext context) {
    this.jwtService = jwtService;
    this.context = context;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    //  Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJraWxsIiwiaWF0IjoxNzIzMTgzNzExLCJleHAiOjE3MjMxODM4MTl9.5nf7dRzKRiuGurN2B9dHh_M5xiu73ZzWPr6rbhOTTHs
    String authHeader = request.getHeader("Authorization");
    String token = null;
    String username = null;

    if (Objects.nonNull(authHeader) && authHeader.startsWith("Bearer ")) {
      token = authHeader.substring(7); // 7 characters for "Bearer ", as to extract the token without the Bearer prefix
      username = jwtService.extractUsername(token);
    }

    if (Objects.nonNull(username) && Objects.isNull(SecurityContextHolder.getContext().getAuthentication())) {
      UserDetails userDetails = context.getBean(UserDetailsServiceImpl.class).loadUserByUsername(username);
      if (jwtService.validateToken(token, userDetails)) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
      }
    }

    filterChain.doFilter(request, response);
  }
}
