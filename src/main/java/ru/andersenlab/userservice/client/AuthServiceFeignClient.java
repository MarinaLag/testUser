package ru.andersenlab.userservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.RequestHeader;
import ru.andersenlab.userservice.config.FeignConfig;
import ru.andersenlab.userservice.domain.dto.BlockRequestDto;
import ru.andersenlab.userservice.domain.dto.ChangeAccessRequestDto;

@FeignClient(name = "auth-service", url = "http://localhost:8080/api/v1/auth",
        configuration = FeignConfig.class)
public interface AuthServiceFeignClient {

    @PatchMapping("/block")
    ResponseEntity<String> blockEmployee(@RequestBody BlockRequestDto blockRequestDto,
                                         @RequestHeader("Authorization") String token);

    @PatchMapping("/change_access")
    ResponseEntity<String> changeAccessLevel(@RequestBody ChangeAccessRequestDto request);
}
