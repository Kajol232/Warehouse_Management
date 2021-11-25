package com.muhammad.warehouse_management.controller;

import com.muhammad.warehouse_management.config.JWTConfig.JWTTokenProvider;
import com.muhammad.warehouse_management.exception.ExceptionHandling;
import com.muhammad.warehouse_management.exception.domain.EmailExistException;
import com.muhammad.warehouse_management.exception.domain.UserNotFoundException;
import com.muhammad.warehouse_management.exception.domain.UsernameExistException;
import com.muhammad.warehouse_management.model.CustomUserDetails;
import com.muhammad.warehouse_management.model.User;
import com.muhammad.warehouse_management.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import static com.muhammad.warehouse_management.config.constant.SecurityConstant.JWT_TOKEN_HEADER;

@RestController
@RequestMapping(path = "/user")
public class UserController extends ExceptionHandling {
    private AuthenticationManager authenticationManager;
    private UserServiceImpl userService;
    private JWTTokenProvider tokenProvider;

    @Autowired
    public UserController(AuthenticationManager authenticationManager, UserServiceImpl userService,
                          JWTTokenProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.tokenProvider = tokenProvider;
    }

    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody User user){
        authenticate(user.getUserName(),user.getPassword());
        User loginUser = userService.findUserByUsername(user.getUserName());
        CustomUserDetails userDetails = new CustomUserDetails(user);
        HttpHeaders headers = getJwtHeader(userDetails);
        return new ResponseEntity<>(loginUser, headers, HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) throws UserNotFoundException, UsernameExistException, EmailExistException {
        User newUser = userService.register(user.getFirstName(), user.getLastName(), user.getUserName(), user.getEmail(),
                user.getRole(), user.getPassword());
        return new ResponseEntity<>(newUser, HttpStatus.OK);
    }

    private HttpHeaders getJwtHeader(CustomUserDetails user) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(JWT_TOKEN_HEADER, tokenProvider.generateJwtToken(user));
        return headers;
    }

    private void authenticate(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }

}
