package gr.uoa.di.ships.configurations;

import gr.uoa.di.ships.configurations.security.JwtService;
import gr.uoa.di.ships.services.interfaces.SeeSeaUserDetailsService;
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

//  @Override
//  public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
//    StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
//    System.out.println("Received WebSocket message with command: " + accessor.getCommand());
//    System.out.println("Authorization Header: " + accessor.getFirstNativeHeader("Authorization"));
//
//    if (StompCommand.CONNECT.equals(accessor.getCommand())) {
//      String token = accessor.getFirstNativeHeader("Authorization");
//      if (Objects.nonNull(token) && token.startsWith("Bearer ")) {
//        token = token.substring(7);
//        String username = jwtService.extractUsername(token);
//        UserDetails userDetails = seeSeaUserDetailsService.loadUserByUsername(username);
//        Long userId = ((RegisteredUser) userDetails).getId();
//        System.out.println("Extracted username: " + username);
//        System.out.println("Setting Principal with userId: " + userId);
//        Principal stompPrincipal = new StompPrincipal(userId);
//        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(stompPrincipal, null, userDetails.getAuthorities());
//        accessor.setUser(authentication);
//        System.out.println("[WebSocket CONNECT] Principal set with name: " + authentication.getName());
//      }
//    }
//    return message;
//  }

  @Override
  public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
    StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
    validateAccessor(accessor);
    if (StompCommand.CONNECT.equals(accessor.getCommand())) {
      List<String> authHeaderList =  accessor.getNativeHeader("Authorization");
      validateAuthHeaderList(authHeaderList);
      String authHeader = authHeaderList.getFirst();
      if (Objects.nonNull(authHeader) && authHeader.startsWith("Bearer ")) {
        String jwt = authHeader.substring(7);
        String username = jwtService.extractUsername(jwt);
        log.info("username: {}", username);
        UserDetails userDetails = seeSeaUserDetailsService.loadUserByUsername(username);
        UsernamePasswordAuthenticationToken authenticatedUser = new UsernamePasswordAuthenticationToken(userDetails,null, userDetails.getAuthorities());
        accessor.setUser(authenticatedUser);

      } else{
        log.info("Authorization header not present");
      }
    }
    return message;
  }

  private static void validateAuthHeaderList(List<String> authHeaderList) {
    log.info("authHeader: {}", authHeaderList);
    if (Objects.isNull(authHeaderList)) {
      throw new IllegalStateException("NativeHeader authHeaderList, could not be retrieved from the accessor.");
    }
  }

  private static void validateAccessor(StompHeaderAccessor accessor) {
    if (Objects.isNull(accessor)) {
      throw new IllegalStateException("StompHeaderAccessor could not be retrieved from the message.");
    }
  }
}
