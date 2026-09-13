package avinash.kumar.dpg.merchant.mapper;

import avinash.kumar.dpg.merchant.dto.request.MerchantSignupRequest;
import avinash.kumar.dpg.merchant.dto.response.MerchantResponse;
import avinash.kumar.dpg.merchant.entity.Merchant;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MerchantMapper {
    Merchant toEntityFromSignUpRequest(MerchantSignupRequest request);
    MerchantResponse toResponse(Merchant merchant);
}
