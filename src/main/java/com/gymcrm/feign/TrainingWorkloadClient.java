package com.gymcrm.feign;

import com.gymcrm.dto.request.TrainerWorkloadRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(
        name = "training-workload-service",
        fallbackFactory = TrainingWorkloadClientFallback.class
)
public interface TrainingWorkloadClient {

    @PostMapping("/api/workload")
    ResponseEntity<Void> updateWorkload(
            @RequestBody TrainerWorkloadRequest request,
            @RequestHeader("Authorization") String token,
            @RequestHeader("X-Transaction-ID") String transactionId
    );

}
