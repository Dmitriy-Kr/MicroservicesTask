package org.gym.workload.service;

import org.gym.workload.dto.WorkloadRequest;
import org.gym.workload.entity.Month;
import org.gym.workload.entity.Trainer;
import org.gym.workload.entity.Year;
import org.gym.workload.exception.ServiceException;
import org.gym.workload.repository.TrainerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class TrainerWorkloadService {
    private final TrainerRepository repository;

    private static final Logger logger = LoggerFactory.getLogger( TrainerWorkloadService.class);

    public TrainerWorkloadService(TrainerRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void process(WorkloadRequest request) throws ServiceException {
        try {
            switch (request.getActionType()) {
                case ADD:
                    add(request);
                    break;
                case DELETE:
                    delete(request);
            }

        } catch (Exception ex) {
            logger.error("Fail to process request");
            throw new ServiceException("Fail to process request", ex);
        }
    }

    public int getDuration(String username, Integer year, Integer month) {
        Optional<Trainer> trainer = repository.findByUsername(username);

        return trainer.map(value -> value.getYears().stream()
                .filter(y -> y.getYearNumber() == year)
                .map(Year::getMonths)
                .flatMap(Collection::stream)
                .filter(m -> m.getMonthNumber() == month)
                .findFirst().orElse(new Month()).getTrainingSummaryDuration()).orElse(-100);
        // need throwing exception here
    }

    private void add(WorkloadRequest request) {
        Optional<Trainer> trainer = repository.findByUsername(request.getTrainerUsername());

        if (trainer.isPresent()) {

            trainer.get().setStatus(request.isActive());

            Year year = trainer.get().getYears()
                    .stream()
                    .filter(y -> y.getYearNumber() == request.getTrainingDate().toLocalDate().getYear())
                    .findAny().orElse(new Year());

            if (year.getYearNumber() != 0) {

                Month month = year.getMonths().stream()
                        .filter(m -> m.getMonthNumber() == request.getTrainingDate().toLocalDate().getMonthValue())
                        .findAny().orElse(new Month());

                if (month.getMonthNumber() != 0) {

                    month.increaseDurationBy(request.getTrainingDuration());

                } else {

                    month.setMonthNumber(request.getTrainingDate().toLocalDate().getMonthValue());
                    month.setTrainingSummaryDuration(request.getTrainingDuration());
                    month.setYear(year);

                    year.getMonths().add(month);

                }

            } else {

                year.setYearNumber(request.getTrainingDate().toLocalDate().getYear());
                year.setTrainer(trainer.get());

                Month newMonth = new Month();
                newMonth.setMonthNumber(request.getTrainingDate().toLocalDate().getMonthValue());
                newMonth.setTrainingSummaryDuration(request.getTrainingDuration());
                newMonth.setYear(year);

                year.setMonths(List.of(newMonth));

                trainer.get().getYears().add(year);

            }

            repository.save(trainer.get());

        } else { //create new Trainer

            Trainer newTrainer = new Trainer();

            newTrainer.setUsername(request.getTrainerUsername());
            newTrainer.setFirstname(request.getTrainerFirstName());
            newTrainer.setLastname(request.getTrainerLastName());
            newTrainer.setStatus(request.isActive());

            Month month = new Month();
            month.setMonthNumber(request.getTrainingDate().toLocalDate().getMonthValue());
            month.setTrainingSummaryDuration(request.getTrainingDuration());

            Year year = new Year();
            year.setYearNumber(request.getTrainingDate().toLocalDate().getYear());
            year.setMonths(List.of(month));
            year.setTrainer(newTrainer);

            month.setYear(year);

            newTrainer.setYears(List.of(year));

            repository.save(newTrainer);

        }
    }

    private void delete(WorkloadRequest request) {
        Optional<Trainer> trainer = repository.findByUsername(request.getTrainerUsername());

        if (trainer.isPresent()) {

            trainer.get().getYears().stream()
                    .filter(y -> y.getYearNumber() == request.getTrainingDate().toLocalDate().getYear())
                    .map(Year::getMonths)
                    .flatMap(Collection::stream)
                    .filter(m -> m.getMonthNumber() == request.getTrainingDate().toLocalDate().getMonthValue())
                    .findFirst()
                    .ifPresent(m -> m.decreaseDurationBy(request.getTrainingDuration()));

            repository.save(trainer.get());

        }
        //need else with throwing exception?
    }
}
