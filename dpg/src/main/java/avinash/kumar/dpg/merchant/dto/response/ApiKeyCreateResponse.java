package avinash.kumar.dpg.merchant.dto.response;

import avinash.kumar.dpg.common.enums.Environment;

import java.util.UUID;

public record ApiKeyCreateResponse(
        UUID id,
        String keyId,
        String keySecret,
        Environment environment
) {
}
