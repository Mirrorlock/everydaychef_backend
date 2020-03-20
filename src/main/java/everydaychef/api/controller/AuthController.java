package everydaychef.api.controller;

import everydaychef.api.config.JwtToken;
import everydaychef.api.model.helpermodels.JwtRequest;
import everydaychef.api.model.helpermodels.JwtResponse;
import everydaychef.api.repository.UserRepository;
import everydaychef.api.service.JwtUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController {

    @Autowired
    private JwtToken jwtToken;

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;
    private final UserRepository userRepository;

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("authenticate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) {
        ResponseEntity<?> result;
        if(authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword())){
            UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(authenticationRequest.getUsername());
            String generatedToken = jwtToken.generateToken(userDetails);
            result = ResponseEntity.ok().body(new JwtResponse(generatedToken));
        }else{
            result = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Wrong credentials");
        }
        System.out.println(result.toString());
        return result;
    }

    private Boolean authenticate(String username, String password) {
        return userRepository.findByName(username)
                .map(user -> new BCryptPasswordEncoder().matches(password, user.getPassword()))
                .orElse(false);
    }

}
