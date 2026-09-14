package avinash.kumar.dpg.merchant.controller;

import avinash.kumar.dpg.merchant.dto.request.LoginRequest;
import avinash.kumar.dpg.merchant.dto.request.MerchantSignupRequest;
import avinash.kumar.dpg.merchant.dto.response.LoginResponse;
import avinash.kumar.dpg.merchant.dto.response.MerchantResponse;
import avinash.kumar.dpg.merchant.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<MerchantResponse> signup(@RequestBody @Valid MerchantSignupRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.signUp(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse>signup(@RequestBody @Valid LoginRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(authService.login(request));
    }
}
