package ru.andersenlab.userservice.service;

import feign.FeignException;
import feign.Request;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.andersenlab.userservice.client.AuthServiceFeignClient;
import ru.andersenlab.userservice.domain.dto.BlockRequestDto;
import ru.andersenlab.userservice.service.impl.FeignAuthService;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FeignAuthServiceTest {
    @Mock
    private AuthServiceFeignClient authServiceFeignClient;

    @InjectMocks
    private FeignAuthService feignAuthService;

    private BlockRequestDto blockRequestDto;

    @BeforeEach
    public void setUp() {
        blockRequestDto = new BlockRequestDto();
        blockRequestDto.setEmployeeId(UUID.randomUUID().toString());
        blockRequestDto.setIsBlocked(true);
    }

    @Test
    public void testRetryOnServerErrorSuccess() {
        ResponseEntity<String> responseEntity = new ResponseEntity<>(HttpStatus.OK);

        when(authServiceFeignClient.blockEmployee(any(BlockRequestDto.class), UUID.randomUUID().toString())).thenReturn(responseEntity);

        feignAuthService.tryCatchResponse(blockRequestDto, UUID.randomUUID().toString());

        verify(authServiceFeignClient, times(1)).blockEmployee(blockRequestDto,UUID.randomUUID().toString());
    }

    @Test
    public void testRetryOnServerErrorSuccessAfterRetries() {
        ResponseEntity<String> responseEntity = new ResponseEntity<>(HttpStatus.OK);
        Request request = Request.create(Request.HttpMethod.GET, "", Map.of(), new byte[0], Charset.defaultCharset(), null);

        when(authServiceFeignClient.blockEmployee(any(BlockRequestDto.class), UUID.randomUUID().toString()))
                .thenThrow(new FeignException.InternalServerError("Server Error", request, null, new HashMap<>()))
                .thenReturn(responseEntity);

        feignAuthService.tryCatchResponse(blockRequestDto,UUID.randomUUID().toString());

        verify(authServiceFeignClient, times(2)).blockEmployee(blockRequestDto,UUID.randomUUID().toString());
    }

    @Test
    public void testRetryOnServerErrorFailure() {
        Request request = Request.create(Request.HttpMethod.GET, "", Map.of(), new byte[0], Charset.defaultCharset(), null);

        when(authServiceFeignClient.blockEmployee(any(BlockRequestDto.class), UUID.randomUUID().toString()))
                .thenThrow(new FeignException.InternalServerError("Server Error", request, null, new HashMap<>()));

        assertThrows(RuntimeException.class, () -> feignAuthService.tryCatchResponse(blockRequestDto, UUID.randomUUID().toString()));

        verify(authServiceFeignClient, times(12)).blockEmployee(blockRequestDto,UUID.randomUUID().toString());
    }

    @Test
    public void testRetryOnServerErrorNonRetryable() {
        Request request = Request.create(Request.HttpMethod.GET, "", Map.of(), new byte[0], Charset.defaultCharset(), null);

        when(authServiceFeignClient.blockEmployee(any(BlockRequestDto.class),UUID.randomUUID().toString()))
                .thenThrow(new FeignException.BadRequest("Bad Request", request, null, new HashMap<>()));

        assertThrows(FeignException.BadRequest.class, () -> feignAuthService.tryCatchResponse(blockRequestDto,UUID.randomUUID().toString()));

        verify(authServiceFeignClient, times(1)).blockEmployee(blockRequestDto,UUID.randomUUID().toString());
    }
}
