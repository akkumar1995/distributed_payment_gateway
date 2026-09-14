package avinash.kumar.dpg.merchant.service;

import avinash.kumar.dpg.common.exception.ResourceNotFoundException;
import avinash.kumar.dpg.common.util.RandomizerUtil;
import avinash.kumar.dpg.merchant.dto.request.CreateApiKeyRequest;
import avinash.kumar.dpg.merchant.dto.response.ApiKeyCreateResponse;
import avinash.kumar.dpg.merchant.dto.response.ApiKeyResponse;
import avinash.kumar.dpg.merchant.entity.ApiKey;
import avinash.kumar.dpg.merchant.entity.Merchant;
import avinash.kumar.dpg.merchant.mapper.ApiKeyMapper;
import avinash.kumar.dpg.merchant.repository.ApiKeyRepository;
import avinash.kumar.dpg.merchant.repository.MerchantRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApiKeyService {
    private final MerchantRepository merchantRepository;
    private final ApiKeyRepository apiKeyRepository;
    private final ApiKeyMapper apiKeyMapper;
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Transactional
    public ApiKeyCreateResponse create(UUID merchantId, CreateApiKeyRequest request){
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("Merchant" , merchantId));
        String keyId = "dpg_"+request.environment().name().toLowerCase()+"_"+ RandomizerUtil.randomBase64(24);
        String rawSecret = RandomizerUtil.randomBase64(40);
        ApiKey apiKey = ApiKey.builder().merchant(merchant)
                .keyId(keyId)
                .keySecretHash(passwordEncoder.encode(rawSecret))
                .environment(request.environment())
                .build();
        apiKey = apiKeyRepository.save(apiKey);
        return new ApiKeyCreateResponse(apiKey.getId(), keyId, rawSecret, request.environment());
    }

    public List<ApiKeyResponse> listByMerchant(UUID merchantId){
        List<ApiKey> apiKeyList = apiKeyRepository.findByMerchant_Id(merchantId);
        return apiKeyMapper.toResponseList(apiKeyList);
    }

    @Transactional
    public void revoke(UUID merchantId, UUID apiKeyId){
        ApiKey apiKey = apiKeyRepository.findById(apiKeyId)
                .filter(key -> key.getMerchant().getId().equals(merchantId))
                .orElseThrow(() -> new ResourceNotFoundException("ApiKey", apiKeyId));
       apiKey.setEnabled(false);
        apiKeyRepository.save(apiKey);
    }

    public ApiKeyCreateResponse rotate(UUID merchantId, UUID apiKeyId){
        ApiKey apiKey = apiKeyRepository.findById(apiKeyId)
                .filter(key -> key.getMerchant().getId().equals(merchantId))
                .orElseThrow(() -> new ResourceNotFoundException("ApiKey", apiKeyId));
        if(!apiKey.isEnabled()) throw new RuntimeException("Cannot rotate a disabled key");
        String newRawSecret = RandomizerUtil.randomBase64(40);
        apiKey.setPreviousKeySecretHash(apiKey.getKeySecretHash());
        apiKey.setKeySecretHash(passwordEncoder.encode(newRawSecret));
        apiKey.setRotatedAt(LocalDateTime.now());
        apiKey.setGracePeriodExpiresAt(LocalDateTime.now().plusHours(24));
        apiKey = apiKeyRepository.save(apiKey);
        return new ApiKeyCreateResponse(apiKey.getId(), apiKey.getKeyId(), newRawSecret, apiKey.getEnvironment());
    }
}
