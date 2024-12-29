package org.gym.basic.feignclient;

import org.gym.workload.dto.WorkloadRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "workload")
public interface WorkloadClient {

    @GetMapping("/workload")
    Long getDuration(@RequestParam("username") String username,
                            @RequestParam("year") Integer year,
                            @RequestParam("month") Integer month);

    @PostMapping("/workload")
    void process(@RequestBody WorkloadRequest request);
}