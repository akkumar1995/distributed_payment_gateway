package avinash.kumar.dpg.merchant.controller;

import avinash.kumar.dpg.merchant.dto.request.CreateApiKeyRequest;
import avinash.kumar.dpg.merchant.dto.response.ApiKeyCreateResponse;
import avinash.kumar.dpg.merchant.dto.response.ApiKeyResponse;
import avinash.kumar.dpg.merchant.security.MerchantContext;
import avinash.kumar.dpg.merchant.service.ApiKeyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/merchants/api-keys")
@RequiredArgsConstructor
public class ApiKeyController {
    private final ApiKeyService apiKeyService;
    private final MerchantContext merchantContext;

    @PostMapping
    public ResponseEntity<ApiKeyCreateResponse> create(@Valid @RequestBody CreateApiKeyRequest request){
        ApiKeyCreateResponse response = apiKeyService.create(merchantContext.getMerchantId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ApiKeyResponse>> listByMerchant(){
        return ResponseEntity.ok(apiKeyService.listByMerchant(merchantContext.getMerchantId()));
    }

    @DeleteMapping("/keyId")
    public ResponseEntity<Void> revoke(@PathVariable UUID keyId){
        apiKeyService.revoke(merchantContext.getMerchantId(),keyId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{keyId}/rotate")
    public ResponseEntity<ApiKeyCreateResponse> rotateKey(@PathVariable UUID keyId){
        return ResponseEntity.ok(apiKeyService.rotate(merchantContext.getMerchantId(),keyId));
    }
}
