package com.snort.controller;

import com.snort.config.jwt.CustomUserDetailsService;
import com.snort.config.helper.JwtUtil;
import com.snort.config.model.JwtRequest;
import com.snort.config.model.JwtResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static com.snort.enums.Constants.BAD_CREDENTIAL;
import static com.snort.enums.Constants.USERNAME_NOT_FOUND;

@RestController
@Slf4j
public class JwtController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    @Autowired
    private JwtUtil jwtUtil;



    @PostMapping("/token")
    public ResponseEntity<?> generateToken(@RequestBody JwtRequest jwtRequest) throws Exception {

        try {
            this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(jwtRequest.getUsername(), jwtRequest.getPassword()));
        } catch (UsernameNotFoundException | DataAccessException e) {
            log.error("username not found");
        }catch (BadCredentialsException e){
            log.error("Bad Credential");
            throw new BadCredentialsException(BAD_CREDENTIAL.getValue());
        }
        UserDetails userDetails = this.customUserDetailsService.loadUserByUsername(jwtRequest.getUsername());
        String token = this.jwtUtil.generateToken(userDetails);
        return ResponseEntity.ok(new JwtResponse(token));
    }

}
