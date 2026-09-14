package avinash.kumar.dpg.merchant.repository;

import avinash.kumar.dpg.common.enums.MerchantStatus;
import avinash.kumar.dpg.merchant.entity.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MerchantRepository extends JpaRepository<Merchant, UUID> {
    boolean existsByEmail(String email);

    List<Merchant> findByStatus(MerchantStatus merchantStatus);
}
