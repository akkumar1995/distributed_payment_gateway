package avinash.kumar.dpg.merchant.service;

import avinash.kumar.dpg.common.exception.ResourceNotFoundException;
import avinash.kumar.dpg.merchant.entity.Customer;
import avinash.kumar.dpg.merchant.repository.CustomerRepository;
import avinash.kumar.dpg.merchant.repository.MerchantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final MerchantRepository merchantRepository;

    public UUID findOrCreate(UUID merchantId, String email, String name, String phone) {
       if(email==null || email.isBlank()){
           return null;
       }
       return customerRepository.findByMerchant_IdAndEmail(merchantId, email).map(Customer::getId)
               .orElseGet(() -> createNew(merchantId, email, name, phone));
    }

    private UUID createNew(UUID merchantId, String email, String name, String phone){
        var merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("Merchant" ,merchantId));
        var customer = Customer.builder()
                .merchant(merchant)
                .email(email)
                .name(name)
                .phone(phone)
                .build();
        customer = customerRepository.save(customer);
        log.info("Customer created via findOrCreate id={} merchantId={} email={}", customer.getId(), merchantId, email);
        return customer.getId();
    }
}
