package org.gym.basic.service;

import org.gym.basic.dto.TrainingDto;
import org.gym.basic.entity.Training;
import org.gym.basic.repository.TrainingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;

@Service
public class TrainingService {

    private TrainingRepository trainingRepository;
    private TraineeService traineeService;
    private TrainerService trainerService;
    private TrainingTypeService trainingTypeService;

    private static Logger logger = LoggerFactory.getLogger( TrainingService.class);

    public TrainingService(TrainingRepository trainingRepository, TraineeService traineeService, TrainerService trainerService, TrainingTypeService trainingTypeService) {
        this.trainingRepository = trainingRepository;
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingTypeService = trainingTypeService;
    }

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

    public void deleteTrainingById(long id){
        trainingRepository.deleteById(id);
    }
}
