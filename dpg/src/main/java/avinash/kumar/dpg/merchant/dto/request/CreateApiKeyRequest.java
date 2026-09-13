package avinash.kumar.dpg.merchant.dto.request;

import avinash.kumar.dpg.common.enums.Environment;

public record CreateApiKeyRequest(
        Environment environment
) {
}
