package gr.uoa.di.ships.configurations.websockets;

import gr.uoa.di.ships.configurations.security.JwtService;
import gr.uoa.di.ships.persistence.model.RegisteredUser;
import gr.uoa.di.ships.services.interfaces.SeeSeaUserDetailsService;
import java.security.Principal;
import java.util.List;
import java.util.Objects;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

@Configuration
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class MessageInterceptor implements ChannelInterceptor {

  private final JwtService jwtService;
  private final SeeSeaUserDetailsService seeSeaUserDetailsService;

  public MessageInterceptor(JwtService jwtService, SeeSeaUserDetailsService seeSeaUserDetailsService) {
    this.jwtService = jwtService;
    this.seeSeaUserDetailsService = seeSeaUserDetailsService;
  }

  @Override
  public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
    StompHeaderAccessor accessor = getStompHeaderAccessor(message);
    if (StompCommand.CONNECT.equals(accessor.getCommand())) {
      List<String> authHeaderList = accessor.getNativeHeader("Authorization");
      handleAuthorizationHeader(authHeaderList, accessor);
    }
    return message;
  }

  private void handleAuthorizationHeader(List<String> authHeaderList, StompHeaderAccessor accessor) {
    if (Objects.nonNull(authHeaderList) && !authHeaderList.isEmpty()) {
      String authHeader = authHeaderList.getFirst();
      processWebSocketConnectRequest(authHeader, accessor);
    } else {
      log.debug("No WS auth header; allowing anonymous STOMP CONNECT");
    }
  }

  private void processWebSocketConnectRequest(String authHeader, StompHeaderAccessor accessor) {
    if (Objects.nonNull(authHeader) && authHeader.startsWith("Bearer ")) {
      String jwt = authHeader.substring(7);
      String username = jwtService.extractUsername(jwt);
      log.info("username: {}", username);
      UserDetails userDetails = seeSeaUserDetailsService.loadUserByUsername(username);
      Long userId = ((RegisteredUser) userDetails).getId();
      Principal idPrincipal = userId::toString;
      UsernamePasswordAuthenticationToken authenticatedUser = new UsernamePasswordAuthenticationToken(idPrincipal, null, userDetails.getAuthorities());
      accessor.setUser(authenticatedUser);
    } else {
      log.info("Authorization header not present");
    }
  }

  private static StompHeaderAccessor getStompHeaderAccessor(Message<?> message) {
    StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
    if (Objects.isNull(accessor)) {
      throw new IllegalStateException("StompHeaderAccessor could not be retrieved from the message.");
    }
    return accessor;
  }
}
