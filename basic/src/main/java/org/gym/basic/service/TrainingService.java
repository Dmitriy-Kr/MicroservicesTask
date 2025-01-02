package org.gym.basic.service;

import org.gym.basic.dto.TrainingDto;
import org.gym.basic.entity.Training;
import org.gym.basic.feignclient.WorkloadClient;
import org.gym.basic.repository.TrainingRepository;
import org.gym.workload.dto.WorkloadRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;

@Service
@Transactional(readOnly = true)
public class TrainingService {

    private final TrainingRepository trainingRepository;
    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingTypeService trainingTypeService;

    private final WorkloadClient workloadClient;

    private static final Logger logger = LoggerFactory.getLogger( TrainingService.class);

    public TrainingService(WorkloadClient workloadClient, TrainingTypeService trainingTypeService, TrainerService trainerService, TraineeService traineeService, TrainingRepository trainingRepository) {
        this.workloadClient = workloadClient;
        this.trainingTypeService = trainingTypeService;
        this.trainerService = trainerService;
        this.traineeService = traineeService;
        this.trainingRepository = trainingRepository;
    }

    @Transactional
    public Training create(TrainingDto trainingDto) throws ServiceException {
        try {
            Training training = new Training();

            training.setTrainingType(trainingTypeService
                    .findByTrainingType(trainingDto.getTrainingType())
                    .orElseThrow(() -> new ServiceException("Fail to create training. No such training type present in DB")));

            training.setTrainee(traineeService
                    .findByUsername(trainingDto.getTraineeUsername())
                    .orElseThrow(() -> new ServiceException("Fail to create training. No such trainee present in DB")));

            training.setTrainer(trainerService
                    .findByUsername(trainingDto.getTrainerUsername())
                    .orElseThrow(() -> new ServiceException("Fail to create training. No such trainer present in DB")));

            training.setTrainingName(trainingDto.getTrainingName());
            training.setTrainingDay(Date.valueOf(LocalDate.parse(trainingDto.getTrainingDay())));
            training.setTrainingDuration(trainingDto.getTrainingDuration());

            String status = workloadClient.process(createRequest(training, WorkloadRequest.ActionType.ADD));

            if(!"successful".equals(status)){
                throw new RuntimeException(status);
            }

            return trainingRepository.save(training);

        } catch (Exception e) {
            logger.error("Fail to create training with trainingName {} ", trainingDto.getTrainingName());
            throw new ServiceException("Fail to create training with trainingName " + trainingDto.getTrainingName() + ". " + e.getMessage(), e);
        }
    }

    public Training getTrainingById(long id) throws ServiceException {
        return trainingRepository.findById(id)
                .orElseThrow(() -> new ServiceException("Fail to find training. No such training present in DB"));
    }

    @Transactional
    public void deleteTrainingById(long id) throws ServiceException {
        Training training = trainingRepository.findById(id)
                .orElseThrow(() -> new ServiceException("Fail to find training. No such training present in DB"));

        WorkloadRequest workloadRequest = createRequest(training, WorkloadRequest.ActionType.DELETE);

        String status = workloadClient.process(workloadRequest);

        if(!"successful".equals(status)){
            throw new ServiceException(status);
        }

        trainingRepository.deleteById(id);
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

        return workloadRequest;
    }
}
