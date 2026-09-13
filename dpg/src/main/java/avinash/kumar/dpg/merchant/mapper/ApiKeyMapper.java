package avinash.kumar.dpg.merchant.mapper;

import avinash.kumar.dpg.merchant.dto.response.ApiKeyCreateResponse;
import avinash.kumar.dpg.merchant.dto.response.ApiKeyResponse;
import avinash.kumar.dpg.merchant.entity.ApiKey;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ApiKeyMapper {
    ApiKeyCreateResponse toCreateResponse(ApiKey apiKey);

    List<ApiKeyResponse> toResponseList(List<ApiKey> apiKeyList);
}
