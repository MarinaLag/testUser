package ru.andersenlab.userservice.service.impl;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.andersenlab.userservice.client.AuthServiceFeignClient;
import ru.andersenlab.userservice.domain.dto.BlockRequestDto;

@Service
@Slf4j
@RequiredArgsConstructor
public class FeignAuthService {
    private final AuthServiceFeignClient authServiceFeignClient;

    @FunctionalInterface
    public interface RetryableFeignCall {
        ResponseEntity<String> execute();
    }

    public void retryOnServerError(RetryableFeignCall call) {
        final int maxRetries = 12;

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                ResponseEntity<String> response = call.execute();
                if (response.getStatusCode().is2xxSuccessful()) {
                    log.info("Request succeeded on attempt {}", attempt);
                    return;
                } else {
                    log.warn("Request failed on attempt {}: status={}, response={}", attempt, response.getStatusCode(), response.getBody());
                }
            } catch (FeignException ex) {
                log.warn("FeignException on attempt {}: status={}, error={}", attempt, ex.status(), ex.getMessage(), ex);
                if (ex.status() != 500) {
                    throw ex;
                }
            }
        }
        throw new RuntimeException("Max retries reached. Please try again later.");
    }

    public void tryCatchResponse(BlockRequestDto blockRequestDto, String token) {
        log.info("Trying to catch response for blockRequestDto: {}", blockRequestDto);
        try {
            retryOnServerError(() -> authServiceFeignClient.blockEmployee(blockRequestDto, token));
        } catch (RuntimeException ex) {
            log.error("Failed to block/unblock employee after retries: {}", ex.getMessage(), ex);
            throw ex;
        }
    }

}
