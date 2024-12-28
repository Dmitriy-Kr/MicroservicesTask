package org.gym.basic.controller;

import org.gym.basic.dto.TrainingDto;
import org.gym.basic.entity.Training;
import org.gym.basic.exception.InvalidDataException;
import org.gym.basic.feignclient.WorkloadClient;
import org.gym.basic.service.ServiceException;
import org.gym.basic.service.TrainingService;
import io.swagger.v3.oas.annotations.Operation;
import org.gym.workload.dto.WorkloadRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import static org.gym.basic.utility.Validation.*;

@Controller
@RequestMapping("/training")
public class TrainingController {

    private TrainingService trainingService;
    private WorkloadClient workloadClient;

    public TrainingController(TrainingService trainingService, WorkloadClient workloadClient) {
        this.trainingService = trainingService;
        this.workloadClient = workloadClient;
    }

    @PostMapping
    @ResponseBody
    @Operation(summary = "Create new Training")
    public void create(@RequestBody TrainingDto trainingDto) throws InvalidDataException, ServiceException {
        validateLogin(trainingDto.getTraineeUsername());

        validateName(trainingDto.getTrainingName());

        validateDate(trainingDto.getTrainingDay());

        Training training = trainingService.create(trainingDto);

        WorkloadRequest workloadRequest = new WorkloadRequest();

        workloadRequest.setActionType(WorkloadRequest.ActionType.ADD);
        workloadRequest.setTrainerUsername(training.getTrainer().getUser().getUsername());
        workloadRequest.setTrainerFirstName(training.getTrainer().getUser().getFirstname());
        workloadRequest.setTrainerLastName(training.getTrainer().getUser().getLastname());
        workloadRequest.setIsActive(training.getTrainer().getUser().isActive());
        workloadRequest.setTrainingDuration(training.getTrainingDuration());
        workloadRequest.setTrainingDate(training.getTrainingDay());

        workloadClient.process(workloadRequest);

    }
}
