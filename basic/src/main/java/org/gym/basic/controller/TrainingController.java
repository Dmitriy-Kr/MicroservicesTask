package org.gym.basic.controller;

import org.gym.basic.dto.TrainingDto;
import org.gym.basic.entity.Training;
import org.gym.basic.exception.InvalidDataException;
import org.gym.basic.feignclient.WorkloadClient;
import org.gym.basic.service.ServiceException;
import org.gym.basic.service.TrainingService;
import io.swagger.v3.oas.annotations.Operation;
import org.gym.workload.dto.WorkloadRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import static org.gym.basic.utility.Validation.*;

@Controller
@RequestMapping("/training")
public class TrainingController {

    private final TrainingService trainingService;
    private final WorkloadClient workloadClient;

    public TrainingController(TrainingService trainingService, WorkloadClient workloadClient) {
        this.trainingService = trainingService;
        this.workloadClient = workloadClient;
    }

    @PostMapping
    @Operation(summary = "Create new Training")
    @ResponseStatus(HttpStatus.OK)
    public void create(@RequestBody TrainingDto trainingDto) throws InvalidDataException, ServiceException {
        validateLogin(trainingDto.getTraineeUsername());

        validateName(trainingDto.getTrainingName());

        validateDate(trainingDto.getTrainingDay());

        Training training = trainingService.create(trainingDto);

        workloadClient.process(createRequest(training, WorkloadRequest.ActionType.ADD));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Training")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable("id") long id) throws ServiceException {
        Training training = trainingService.getTrainingById(id);
        WorkloadRequest workloadRequest = createRequest(training, WorkloadRequest.ActionType.DELETE);
        trainingService.deleteTrainingById(id);
        workloadClient.process(workloadRequest);
    }

    private WorkloadRequest createRequest(Training training, WorkloadRequest.ActionType actionType) {
        WorkloadRequest workloadRequest = new WorkloadRequest();

        workloadRequest.setActionType(actionType);
        workloadRequest.setTrainerUsername(training.getTrainer().getUser().getUsername());
        workloadRequest.setTrainerFirstName(training.getTrainer().getUser().getFirstname());
        workloadRequest.setTrainerLastName(training.getTrainer().getUser().getLastname());
        workloadRequest.setActive(training.getTrainer().getUser().isActive());
        workloadRequest.setTrainingDuration(training.getTrainingDuration());
        workloadRequest.setTrainingDate(training.getTrainingDay());

        System.out.println("Status " + workloadRequest.isActive());

        return workloadRequest;
    }
}
