package tech.getarrays.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.getarrays.domain.HttpResponse;
import tech.getarrays.domain.AppUser;
import tech.getarrays.domain.UserPrincipal;
import tech.getarrays.exception.ExceptionHandling;
import tech.getarrays.exception.domain.EmailExistException;
import tech.getarrays.exception.domain.EmailNotFoundException;
import tech.getarrays.exception.domain.UserNotFoundException;
import tech.getarrays.exception.domain.UsernameExistException;
import tech.getarrays.service.UserService;
import tech.getarrays.utility.JWTTokenProvider;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import static org.springframework.http.HttpStatus.OK;
import static tech.getarrays.constant.SecurityConstant.JWT_TOKEN_HEADER;

@RestController
@RequestMapping(path = {"/", "/user"})
public class UserResource extends ExceptionHandling {
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JWTTokenProvider jWTTokenProvider;

    @Autowired
    public UserResource(AuthenticationManager authenticationManager, UserService userService, JWTTokenProvider jWTTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jWTTokenProvider = jWTTokenProvider;
    }

    @PostMapping("/login")
    public ResponseEntity<AppUser> login(@RequestBody AppUser user) {
        authenticate(user.getUsername(), user.getPassword());
        AppUser loginUser = userService.findUserByUsername(user.getUsername());
        UserPrincipal principal = new UserPrincipal(loginUser);
        HttpHeaders jwtHeader = getJwtHeader(principal);
        return ResponseEntity.ok().headers(jwtHeader).body(loginUser);
    }

    @PostMapping("/register")
    public ResponseEntity<AppUser> register(@RequestBody AppUser user) throws UsernameExistException, EmailExistException, UserNotFoundException {
        AppUser newUser = userService.register(user.getFirstName(), user.getLastName(), user.getUsername(), user.getEmail(),user.getPassword());
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/user/find/" + newUser.getUsername()).toUriString());
        return ResponseEntity.created(uri).body(newUser);
    }

    @PostMapping("/add")
    public ResponseEntity<AppUser> addNewUser(@RequestParam("firstName") String firstName,
                                              @RequestParam("lastName") String lastName,
                                              @RequestParam("username") String username,
                                              @RequestParam("email") String email,
                                              @RequestParam("role") String role,
                                              @RequestParam("isActive") String isActive,
                                              @RequestParam("isNonLocked") String isNonLocked,
                                              @RequestParam(value = "profileImage", required = false) MultipartFile profileImage) throws UsernameExistException, EmailExistException, UserNotFoundException, IOException {
        AppUser newUser = userService.addNewUser(firstName, lastName, username, email, role, Boolean.parseBoolean(isNonLocked),
                Boolean.parseBoolean(isActive), profileImage);
        return ResponseEntity.ok().body(newUser);
    }

    @PostMapping("/update")
    public ResponseEntity<AppUser> update(@RequestParam("currentUsername") String currentUsername,
                                          @RequestParam("firstName") String firstName,
                                          @RequestParam("lastName") String lastName,
                                          @RequestParam("username") String username,
                                          @RequestParam("email") String email,
                                          @RequestParam(value = "role", required = false) String role,
                                          @RequestParam("isActive") String isActive,
                                          @RequestParam("isNonLocked") String isNonLocked) throws UsernameExistException, EmailExistException, UserNotFoundException, IOException {
        AppUser newUser = userService.updateUser(currentUsername, firstName, lastName, username, email, role, Boolean.parseBoolean(isNonLocked),
                Boolean.parseBoolean(isActive));
        return ResponseEntity.ok().body(newUser);
    }

    @GetMapping("/find/{username}")
    public ResponseEntity<AppUser> getUser(@PathVariable("username")String username) {
        AppUser user = userService.findUserByUsername(username);
        return ResponseEntity.ok().body(user);
    }

    @GetMapping("/list")
    public ResponseEntity<List<AppUser>> getAllUsers() {
        List<AppUser> users = userService.getUsers();
        return ResponseEntity.ok().body(users);
    }

	@GetMapping("/resetpassword/{email}")
	public ResponseEntity<HttpResponse> resetPassword(@PathVariable("email") String email) throws EmailNotFoundException {
		userService.resetPassword(email);
		return response(OK, "Password changed successfully");
	}

    @DeleteMapping("/delete/{username}")
    @PreAuthorize("hasAuthority('user:delete')")
    public ResponseEntity<HttpResponse> deleteUser(@PathVariable("username") String username) throws IOException {
        userService.deleteUser(username);
        return response(OK, "User deleted successfully");
    }

    private HttpHeaders getJwtHeader(UserPrincipal user){
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JWT_TOKEN_HEADER, jWTTokenProvider.generateJwtToken(user));
        return httpHeaders;
    }

    private void authenticate(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }

    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
        return new ResponseEntity<>(new HttpResponse(httpStatus.value(),
                httpStatus, httpStatus.getReasonPhrase().toUpperCase(), message.toUpperCase()), httpStatus);
    }

}
