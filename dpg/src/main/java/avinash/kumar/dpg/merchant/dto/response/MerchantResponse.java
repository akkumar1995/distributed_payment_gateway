package avinash.kumar.dpg.merchant.dto.response;

import avinash.kumar.dpg.common.enums.BusinessType;
import avinash.kumar.dpg.common.enums.MerchantStatus;

import java.util.UUID;

public record MerchantResponse(
        UUID id,
        String name,
        String email,
        String businessName,
        BusinessType businessType,
        MerchantStatus merchantStatus
) {
}
