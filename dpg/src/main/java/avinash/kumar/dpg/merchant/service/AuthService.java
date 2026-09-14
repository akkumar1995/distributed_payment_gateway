package avinash.kumar.dpg.merchant.service;

import avinash.kumar.dpg.common.enums.UserRole;
import avinash.kumar.dpg.common.exception.DuplicateResourceException;
import avinash.kumar.dpg.common.exception.ResourceNotFoundException;
import avinash.kumar.dpg.merchant.dto.request.LoginRequest;
import avinash.kumar.dpg.merchant.dto.request.MerchantSignupRequest;
import avinash.kumar.dpg.merchant.dto.response.LoginResponse;
import avinash.kumar.dpg.merchant.dto.response.MerchantResponse;
import avinash.kumar.dpg.merchant.entity.AppUser;
import avinash.kumar.dpg.merchant.entity.Merchant;
import avinash.kumar.dpg.merchant.mapper.MerchantMapper;
import avinash.kumar.dpg.merchant.repository.AppUserRepository;
import avinash.kumar.dpg.merchant.repository.MerchantRepository;
import avinash.kumar.dpg.merchant.security.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {
    private final MerchantRepository merchantRepository;
    private final MerchantMapper merchantMapper;
    private final PasswordEncoder passwordEncoder;
    private final AppUserRepository appUserRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Transactional
    public MerchantResponse signUp(MerchantSignupRequest request){
        if(merchantRepository.existsByEmail(request.email())){
            throw new DuplicateResourceException("DUPLICATE_MERCHANT_EMAIL", "Merchant with email already exists: " + request.email());
        }
        Merchant merchant = merchantMapper.toEntityFromSignUpRequest(request);
        merchant = merchantRepository.save(merchant);
        AppUser appUser = AppUser.builder().merchant(merchant).email(request.email())
                .passwordHash(passwordEncoder.encode(request.password())).role(UserRole.OWNER).build();
        appUserRepository.save(appUser);
        return merchantMapper.toResponse(merchant);
    }

    public LoginResponse login(LoginRequest loginRequest){
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.email(),loginRequest.password()));
        AppUser appUser = appUserRepository.findByEmail(loginRequest.email()).orElseThrow(() -> new ResourceNotFoundException("User",loginRequest.email()));
        String token = jwtUtil.generateAccessToken(loginRequest.email(), appUser.getMerchant().getId(),appUser.getRole().toString());
        return new LoginResponse(token);
    }
}
