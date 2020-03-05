package everydaychef.api.controller;

import everydaychef.api.config.JwtToken;
import everydaychef.api.model.helpermodels.JwtRequest;
import everydaychef.api.model.helpermodels.JwtResponse;
import everydaychef.api.repository.UserRepository;
import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
//@CrossOrigin
public class AuthController {
//    @Autowired
//    private AuthenticationManager authenticationManager;
//
//    @Autowired
//    private JwtToken jwtToken;
//
//    @Autowired
//    private JwtUserDetailsService jwtUserDetailsService;
    private final UserRepository userRepository;

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) {
        ResponseEntity<?> result;
        if(authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword())){
            result = ResponseEntity.ok(new JwtResponse( "toBeTokenInTheFuture..."));
        }else{
            result = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Wrong credentials");
        }
        return result;
//        final UserDetails userDetails = jwtUserDetailsService
//                .loadUserByUsername(authenticationRequest.getUsername());
//        final String token = jwtToken.generateToken(userDetails);
//        return ResponseEntity.ok(new JwtResponse(token));
    }

    private Boolean authenticate(String username, String password) {
        return userRepository.findByName(username)
                .map(user -> new BCryptPasswordEncoder().matches(password, user.getPassword()))
                .orElse(false);
//        try {
//            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
//        } catch (DisabledException e) {
//            throw new Exception("USER_DISABLED", e);
//        } catch (BadCredentialsException e) {
//            throw new Exception("INVALID_CREDENTIALS", e);
//        }
    }

}
