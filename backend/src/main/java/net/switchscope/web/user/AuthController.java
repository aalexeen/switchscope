package net.switchscope.web.user;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import net.switchscope.model.User;
import net.switchscope.security.permission.AuthenticatedOnly;
import net.switchscope.to.LoginResponseTo;
import net.switchscope.web.AuthUser;

import java.util.Map;

@RestController
@RequestMapping(value = AuthController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@AuthenticatedOnly(reason = "authentication self-service: the caller must be able to ask who they are "
        + "and what they may do before any permission can be evaluated")
public class AuthController extends AbstractUserController {

    static final String REST_URL = "/api/auth";

    @PostMapping("/login")
    public ResponseEntity<LoginResponseTo> login(@AuthenticationPrincipal AuthUser authUser) {
        log.info("login user {}", authUser.getUser().getEmail());
        // If this method is reached, authentication was successful
        return ResponseEntity.ok(identityOf(authUser));
    }

    @GetMapping("/check")
    public ResponseEntity<LoginResponseTo> checkAuth(@AuthenticationPrincipal AuthUser authUser) {
        log.info("check auth for user {}", authUser.getUser().getEmail());
        // For checking if current session/credentials are still valid
        return ResponseEntity.ok(identityOf(authUser));
    }

    @GetMapping("/profile")
    public User getProfile(@AuthenticationPrincipal AuthUser authUser) {
        log.info("get profile for user {}", authUser.getUser().getEmail());
        return authUser.getUser();
    }

    /**
     * Both answers are the same answer to the same question, so they are built in one place: login
     * and check differ in when the client asks, not in what it gets back.
     * <p>
     * The permissions come from the authenticated principal rather than from the grant table,
     * which is what makes them true - they are the very authorities the authorization advisor will
     * match a request against, cache staleness and all.
     */
    private static LoginResponseTo identityOf(AuthUser authUser) {
        User user = authUser.getUser();
        return new LoginResponseTo(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRoles(),
                authUser.permissions()
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(@AuthenticationPrincipal AuthUser authUser) {
        log.info("logout user {}", authUser.getUser().getEmail());
        // With HTTP Basic and stateless sessions, logout is primarily client-side
        // This endpoint mainly serves for logging and potential future server-side logout logic
        Map<String, String> response = Map.of(
                "message", "Logout successful",
                "timestamp", java.time.LocalDateTime.now().toString()
        );
        return ResponseEntity.ok(response);
    }
}
