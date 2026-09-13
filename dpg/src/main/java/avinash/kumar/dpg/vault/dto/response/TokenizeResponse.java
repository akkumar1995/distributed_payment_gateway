package avinash.kumar.dpg.vault.dto.response;

import avinash.kumar.dpg.common.enums.CardBrand;

public record TokenizeResponse(
        String token,
        String lastFour,
        CardBrand brand,
        Integer expiryMonth,
        Integer expiryYear
) {
}
