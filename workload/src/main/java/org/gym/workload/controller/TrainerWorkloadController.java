package org.gym.workload.controller;

import jakarta.validation.Valid;
import org.gym.workload.dto.WorkloadRequest;
import org.gym.workload.service.TrainerWorkloadService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("workload")
public class TrainerWorkloadController {
    private final TrainerWorkloadService service;

    public TrainerWorkloadController(TrainerWorkloadService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public void process(@Valid @RequestBody WorkloadRequest request) {
        service.process(request);
    }

    @GetMapping
    @ResponseBody
    public int getDuration(@RequestParam("username") String username,
                            @RequestParam("year") Integer year,
                            @RequestParam("month") Integer month) {

        return service.getDuration(username, year, month);

    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        return ResponseEntity.badRequest().body(errors);
    }

}