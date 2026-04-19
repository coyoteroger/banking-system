package com.devsuv.account.infrastructure.messaging;

import com.devsuv.account.domain.model.Account;
import com.devsuv.account.domain.model.CustomerInfo;
import com.devsuv.account.domain.port.AccountRepository;
import com.devsuv.account.domain.port.CustomerInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomerEventListener {

    private final CustomerInfoRepository customerInfoRepository;
    private final AccountRepository accountRepository;

    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    @Transactional
    public void handleCustomerEvent(CustomerEventDTO event) {
        log.info("Received customer event: operation={}, customerId={}", event.getOperation(), event.getCustomerId());

        switch (event.getOperation()) {
            case "CREATED" -> handleCreated(event);
            case "UPDATED" -> handleUpdated(event);
            case "DELETED" -> handleDeleted(event);
            default -> log.warn("Unknown customer event operation: {}", event.getOperation());
        }
    }

    private void handleCreated(CustomerEventDTO event) {
        customerInfoRepository.findByCustomerId(event.getCustomerId()).ifPresentOrElse(
                existing -> log.warn("CustomerInfo already exists for customerId: {}, skipping creation", event.getCustomerId()),
                () -> {
                    CustomerInfo info = CustomerInfo.builder()
                            .customerId(event.getCustomerId())
                            .name(event.getName())
                            .status(event.getStatus())
                            .build();
                    customerInfoRepository.save(info);
                    log.info("CustomerInfo created for customerId: {}", event.getCustomerId());
                }
        );
    }

    private void handleUpdated(CustomerEventDTO event) {
        customerInfoRepository.findByCustomerId(event.getCustomerId()).ifPresent(info -> {
            info.setName(event.getName());
            info.setStatus(event.getStatus());
            customerInfoRepository.save(info);
            log.info("CustomerInfo updated for customerId: {}", event.getCustomerId());
        });
    }

    private void handleDeleted(CustomerEventDTO event) {
        customerInfoRepository.findByCustomerId(event.getCustomerId()).ifPresent(info -> {
            info.setStatus(false);
            customerInfoRepository.save(info);
            log.info("CustomerInfo deactivated for customerId: {}", event.getCustomerId());
        });

        List<Account> accounts = accountRepository.findByCustomerId(event.getCustomerId());
        accounts.forEach(account -> {
            account.setStatus(false);
            accountRepository.save(account);
        });
        log.info("Deactivated {} account(s) for customerId: {}", accounts.size(), event.getCustomerId());
    }
}
