package avinash.kumar.dpg.merchant.security;

import avinash.kumar.dpg.common.exception.ResourceNotFoundException;
import avinash.kumar.dpg.merchant.repository.AppUserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MerchantUserDetailsService implements UserDetailsService {
    private final AppUserRepository appUserRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        appUserRepository.findByEmail(email).orElseThrow(()->new ResourceNotFoundException("User",email));
    }
}
