package avinash.kumar.dpg.merchant.security;

import avinash.kumar.dpg.common.exception.ResourceNotFoundException;
import avinash.kumar.dpg.merchant.entity.ApiKey;
import avinash.kumar.dpg.merchant.repository.ApiKeyRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {
    private static final String BASIC = "Basic ";
    private final HandlerExceptionResolver handlerExceptionResolver;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final ApiKeyRepository apiKeyRepository;
    private final MerchantContext merchantContext;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("Incoming Request: {}",request.getRequestURI());
        try{
            String header = request.getHeader("Authorization");
            if(header==null || !header.startsWith(BASIC)){
                filterChain.doFilter(request,response);
                return;
            }
            String[] credentials = decode(header);
            if(credentials==null){
                throw new BadRequestException("Bad Key Header");
            }
            String keyId = credentials[0];
            String rawSecret = credentials[1];
            ApiKey apiKey = apiKeyRepository.findByKeyId(keyId).orElseThrow(() -> new ResourceNotFoundException("API_KEY_NOT_FOUND","Api Key not found"));
            if(apiKey==null || !apiKey.isEnabled() || !secretMatches(rawSecret,apiKey)){
                throw new BadRequestException("Inavid or missing Api Key");
            }
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(keyId,null, List.of(new SimpleGrantedAuthority("API_KEY_ROLE")));
            SecurityContextHolder.getContext().setAuthentication(auth);
            merchantContext.setMerchantId(apiKey.getMerchant().getId());
            merchantContext.setKeyId(apiKey.getKeyId());
            filterChain.doFilter(request,response);
        }catch(Exception e){
            handlerExceptionResolver.resolveException(request,response,null,e);
        }
    }

    private String[] decode(String header){
        String encoded = header.substring(BASIC.length());
        String decoded = new String(Base64.getDecoder().decode(encoded), StandardCharsets.UTF_8);
        int colon = decoded.indexOf(":");
        if(colon<1) return null;
        return new String[]{decoded.substring(0,colon),decoded.substring(colon)};
    }

    public boolean isInGracePeriod(LocalDateTime gracePeriodExpiresAt) {
        return gracePeriodExpiresAt != null && LocalDateTime.now().isBefore(gracePeriodExpiresAt);
    }

    private boolean secretMatches(String rawSecret, ApiKey apiKey){
        if(passwordEncoder.matches(rawSecret, apiKey.getKeySecretHash())) return true;
        return isInGracePeriod(apiKey.getGracePeriodExpiresAt()) && apiKey.getPreviousKeySecretHash() != null &&
                passwordEncoder.matches(rawSecret, apiKey.getPreviousKeySecretHash());
    }
}
