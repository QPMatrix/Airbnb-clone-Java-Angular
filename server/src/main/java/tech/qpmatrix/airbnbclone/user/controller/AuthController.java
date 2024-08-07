package tech.qpmatrix.airbnbclone.user.controller;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tech.qpmatrix.airbnbclone.user.application.UserService;
import tech.qpmatrix.airbnbclone.user.application.dto.ReadUserDTO;

import java.text.MessageFormat;
import java.util.Map;

@RestController
@RequestMapping("auth")
public class AuthController {
    private final UserService userService;
    private final ClientRegistration registration;

    public AuthController(UserService userService, ClientRegistrationRepository registration) {
        this.userService = userService;
        this.registration = registration.findByRegistrationId("okta");
    }
    @GetMapping("/get-authenticated-user")
    public ResponseEntity<ReadUserDTO> getAuthenticatedUser(@AuthenticationPrincipal OAuth2User user, @RequestParam boolean forceReSync) {
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else {
            userService.syncWithIdp(user, forceReSync);
            ReadUserDTO connectedUser = userService.getAuthenticatedUser();
            return new ResponseEntity<>(connectedUser, HttpStatus.OK);
        }
    }

    public ResponseEntity<Map<String , String>> logout(HttpServletRequest request){
       String issuerUrl = registration.getProviderDetails().getIssuerUri();
       String originUrl =request.getHeader(HttpHeaders.ORIGIN);
       Object[] parmas ={ issuerUrl, registration.getClientId() , originUrl};
       String logoutUrl = MessageFormat.format("{0}v2/logout?client_id={1}&returnTo={2}", parmas);
       request.getSession().invalidate();
       return ResponseEntity.ok().body(Map.of("logoutUrl",logoutUrl));
    }
}
