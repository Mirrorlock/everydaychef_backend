package everydaychef.api.config;

import com.google.api.client.auth.oauth2.BearerToken;
import io.jsonwebtoken.ExpiredJwtException;
import net.minidev.json.JSONObject;
import org.hibernate.tool.schema.internal.exec.ScriptTargetOutputToFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
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
//    @Autowired
//    private JwtUserDetailsService jwtUserDetailsService;

    private final JwtToken jwtTokenUtil;

    @Value("${google.client.id}")
    private String googleClientId;

    public JwtRequestFilter(JwtToken jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        Authentication authentication = getAuthentication(request);
        if (authentication != null) {
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        chain.doFilter(request, response);
    }

    private Authentication getAuthentication(HttpServletRequest request) {
        final String requestTokenHeader = request.getHeader("Authorization");
        final String authenticationMethod = request.getHeader("AuthenticationMethod");
        String username = null;
        String jwtToken;
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7);
            if (authenticationMethod != null && authenticationMethod.equals("Google")) {
                return getAuthenticationFromGoogle(jwtToken);
            } else if (authenticationMethod != null && authenticationMethod.equals("Facebook")) {
//                getAuthenticationFromFacebook(jwtToken);
//                jwtTokenUtil.validateFacebookToken(jwtToken);
            } else {
//                getManualAuthentication(jwtToken);
//                jwtTokenUtil.validateManualToken(jwtToken);
            }
//                try {
//                    username = jwtTokenUtil.getUsernameFromToken(jwtToken);
//                } catch (IllegalArgumentException e) {
//                    logger.error("Unable to get JWT Token");
//                } catch (ExpiredJwtException e) {
//                    logger.error("JWT Token has expired");
//                }
//                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//                    UserDetails userDetails = this.jwtUserDetailsService.loadUserByUsername(username);
//                    System.out.println(userDetails.toString());
//                    if (jwtTokenUtil.validateManualToken(jwtToken, userDetails)) {
//                        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
//                                userDetails, null, userDetails.getAuthorities());
//                        usernamePasswordAuthenticationToken
//                                .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
//                    }
//                }
        } else {
            logger.warn("JWT Token does not begin with Bearer String");
        }
        return null;
    }

    private Authentication getAuthenticationFromGoogle(String jwtToken) {
        JSONObject googleTokenResponse = jwtTokenUtil.validateGoogleTokenResponse(jwtToken);
        String audClaim = googleTokenResponse.getAsString("aud");
        if (audClaim.equals(googleClientId)) {
            return null;
        }   else{
            return null;
        }

    }
}