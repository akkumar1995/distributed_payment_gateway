package avinash.kumar.dpg.merchant.entity;

import avinash.kumar.dpg.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name ="merchant_webhook_config",
        indexes = {
        @Index(name="idx_webhook_merchant_id", columnList = "merchant_id, enabled")
        }
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MerchantWebHookConfig extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="merchant_id", nullable = false)
    private Merchant merchant;
    @Column(nullable = false, length = 500)
    private String targetUrl;
    @Column(length = 255)
    private String webhookSecret;
    @Column(nullable = false)
    private Boolean enabled = true;
    @Column(length = 255)
    private String eventTypes;

    public Boolean isSubscribedTo(String eventType){
        if(eventType==null || eventType.isEmpty()){
            return true;
        }
        for(String type:eventType.split(",")){
            String trimmed = type.trim();
            if(trimmed.equalsIgnoreCase("ALL") || trimmed.equalsIgnoreCase(eventType)){
                return true;
            }
        }
        return false;
    }
}
