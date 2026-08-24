package net.switchscope.web.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import net.switchscope.mapper.UserMapper;
import net.switchscope.model.User;
import net.switchscope.security.permission.AuthenticatedOnly;
import net.switchscope.service.UserService;
import net.switchscope.to.UserTo;
import net.switchscope.web.AuthUser;

import java.net.URI;

import static net.switchscope.validation.ValidationUtil.assureIdConsistent;
import static net.switchscope.validation.ValidationUtil.checkNew;

@RestController
@RequestMapping(value = ProfileController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@RequiredArgsConstructor
@AuthenticatedOnly(reason = "profile self-service: a user reading, updating or deleting their own "
        + "account is not exercising a grantable permission")
public class ProfileController extends AbstractUserController {
    static final String REST_URL = "/api/profile";

    private final UserMapper mapper;
    private final UserService userService;

    @GetMapping
    public User get(@AuthenticationPrincipal AuthUser authUser) {
        log.info("get {}", authUser);
        return authUser.getUser();
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal AuthUser authUser) {
        super.delete(authUser.id());
    }

    /**
     * The only endpoint under {@code /api} an unauthenticated caller may reach; the filter chain
     * permits it explicitly. The e-mail is kept unique by {@code UniqueMailValidator}, registered
     * for this body in {@link AbstractUserController#initBinder}, and the role comes from
     * {@code UserMapper}, not from the request - a caller who may not authenticate certainly may
     * not choose what they are.
     * <p>
     * Creation goes through {@link UserService#create} rather than straight to the repository, so
     * that whatever creating a user comes to mean happens here too. The uniqueness check inside it
     * is then the second one on this path: the validator answers first and with a field error, and
     * this one stands for the case the validator cannot see, two requests racing for the same
     * address.
     */
    @AuthenticatedOnly(reason = "registration: reachable without authentication, so there is no "
            + "caller yet to hold a permission - the frontend offers it from the login screen")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<User> register(@Valid @RequestBody UserTo userTo) {
        log.info("register {}", userTo);
        checkNew(userTo);
        User created = userService.create(mapper.toEntity(userTo));
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL).build().toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@RequestBody @Valid UserTo userTo, @AuthenticationPrincipal AuthUser authUser) {
        log.info("update {} with id={}", userTo, authUser.id());
        assureIdConsistent(userTo, authUser.id());
        userService.updateFromTo(authUser.getUser(), userTo);
    }
}
