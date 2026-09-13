package avinash.kumar.dpg.payment.gateway.dto;

import avinash.kumar.dpg.common.entity.Money;
import avinash.kumar.dpg.common.enums.PaymentMethod;

import java.util.Map;
import java.util.UUID;

public record PaymentRequest(
        UUID paymentId,
        UUID orderId,
        UUID merchantId,
        Money amount,
        PaymentMethod method,
        Map<String, Object> methodDetails
) {
}
