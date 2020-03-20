package everydaychef.api.config;

import everydaychef.api.repository.UserRepository;
import everydaychef.api.service.JwtUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    private final UserRepository userRepository;

    private final JwtToken jwtTokenUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Value("${google.client.id}")
    private String googleClientId;

    public JwtRequestFilter(UserRepository userRepository, JwtToken jwtTokenUtil) {
        this.userRepository = userRepository;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String name = getNameIfAuthenticated(request);
        if (name != null) {
            UserDetails userDetails = this.jwtUserDetailsService.loadUserByUsername(name);
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
            usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        }
        chain.doFilter(request, response);
    }

    private String getNameIfAuthenticated(HttpServletRequest request) {
        final String requestTokenHeader = request.getHeader("Authorization");
        final String authenticationMethod = request.getHeader("AuthenticationMethod");
        System.out.println("Found token: " + requestTokenHeader);
        System.out.println("Found method: " + authenticationMethod);
        String jwtToken;
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7);
            if (authenticationMethod != null && authenticationMethod.equals("Google")) {
                return getNameFromGoogle(jwtToken);
            } else if (authenticationMethod != null && authenticationMethod.equals("Facebook")) {
                return getNameFromFacebook(jwtToken);
            } else {
                try {
                    if(jwtTokenUtil.validateManualToken(jwtToken))
                        return jwtTokenUtil.getUsernameFromToken(jwtToken);
                } catch (IllegalArgumentException e) {
                    logger.error("Unable to get JWT Token");
                } catch (ExpiredJwtException e) {
                    logger.error("JWT Token has expired");
                }
            }
        } else {
            logger.warn("JWT Token does not begin with Bearer String");
        }
        return null;
    }

    private String getNameFromGoogle(String jwtToken) {
        JSONObject googleTokenResponse = jwtTokenUtil.validateGoogleToken(jwtToken);
        String audClaim = googleTokenResponse.getAsString("aud");
        String name = googleTokenResponse.getAsString("name");
        if (audClaim != null && audClaim.equals(googleClientId)) {
            return name;
        }else{
            return null;
        }
    }

    private String getNameFromFacebook(String jwtToken) {
        JSONObject facebookTokenResponse = jwtTokenUtil.validateFacebookToken(jwtToken);
        System.out.println(facebookTokenResponse);
        return facebookTokenResponse.getAsString("name");
    }



}