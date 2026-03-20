package com.example.COFFEEHOUSE.Controller;


import com.example.COFFEEHOUSE.Config.Createjwt;
import com.example.COFFEEHOUSE.DTO.Request.LoginRequest;
import com.example.COFFEEHOUSE.DTO.ResponseData;
import com.example.COFFEEHOUSE.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final Createjwt jwtCustom;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        HttpStatus status = HttpStatus.OK;
        ResponseData responseData = new ResponseData();

        try {
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());
            Authentication authentication = authenticationManager.authenticate(authenticationToken);

            if (authentication.isAuthenticated()) {
                SecurityContextHolder.getContext().setAuthentication(authentication);
                String token = jwtCustom.createToken(authentication);
                responseData.setSuccess(true);
                responseData.setMessage("Đăng nhập thành công");
                responseData.setData(token);
            }
        } catch (BadCredentialsException e) {
            status = HttpStatus.UNAUTHORIZED;
            responseData.setSuccess(false);
            responseData.setMessage("Tài khoản hoặc mật khẩu không đúng");
            responseData.setData(null);
        }

        return ResponseEntity.status(status).body(responseData);
    }
//    @PostMapping("/signup")
//    public ResponseEntity<?> register(@RequestBody SignupRequest req){
//        HttpStatus status = HttpStatus.OK;
//        userService.RegisterUser(req);
//        Authentication authentication = authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(req.getUserName(), req.getPassword()));
//        String token = jwtCustom.createToken(authentication);
//        ResponseData resp = ResponseData.builder()
//                .message("Register successfully")
//                .success(true)
//                .data(token)
//                .build();
//        return ResponseEntity.status(status).body(resp);
//    }

}