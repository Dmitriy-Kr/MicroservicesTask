package org.gym.workload.service;

import org.gym.workload.dto.WorkloadRequest;
import org.gym.workload.entity.TrainerWorkload;
import org.gym.workload.repository.TrainerWorkloadRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class TrainerWorkloadService {
    private final TrainerWorkloadRepository repository;

    public TrainerWorkloadService(TrainerWorkloadRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void process(WorkloadRequest request) {
        switch (request.getActionType()) {
            case ADD:
                add(request);
                break;
            case DELETE:
                delete(request);
        }
    }

    public Long getDuration(String username, Integer year, Integer month){
        Optional<TrainerWorkload> trainerWorkload = repository.findByUsername(username);

        if (trainerWorkload.isPresent()) {
            return trainerWorkload.get().getDuration();
        }
        // need throwing exception here
        return -100L;
    }

    private void add(WorkloadRequest request) {
        Optional<TrainerWorkload> trainerWorkload = repository.findByUsername(request.getTrainerUsername());

        if (trainerWorkload.isPresent()) {

            Long duration = trainerWorkload.get().getDuration();
            duration += request.getTrainingDuration();
            trainerWorkload.get().setDuration(duration);
            repository.save(trainerWorkload.get());

        } else {

            TrainerWorkload newTrainerWorkload = new TrainerWorkload();

            newTrainerWorkload.setUsername(request.getTrainerUsername());
            newTrainerWorkload.setFirstname(request.getTrainerFirstName());
            newTrainerWorkload.setLastname(request.getTrainerLastName());
            newTrainerWorkload.setStatus(request.isActive());
            newTrainerWorkload.setDuration(request.getTrainingDuration().longValue());

            repository.save(newTrainerWorkload);

        }
    }

    private void delete(WorkloadRequest request) {
        Optional<TrainerWorkload> trainerWorkload = repository.findByUsername(request.getTrainerUsername());

        if (trainerWorkload.isPresent()) {

            Long duration = trainerWorkload.get().getDuration();
            duration -= request.getTrainingDuration();
            trainerWorkload.get().setDuration(duration < 0 ? 0 : duration);

            repository.save(trainerWorkload.get());

        }
        //need else with throwing exception
    }
}
