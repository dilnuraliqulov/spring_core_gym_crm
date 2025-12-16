package com.gymcrm.repository;

import com.gymcrm.entity.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface TrainingRepository extends JpaRepository<Training, Long> {

    List<Training> findByTraineeId(Long traineeId);

    List<Training> findByTrainerId(Long trainerId);

    List<Training> findByTrainingTypeId(Long trainingTypeId);

    List<Training> findByTrainingDate(Date trainingDate);

    @Query("SELECT t FROM Training t WHERE t.trainee.user.username = :traineeUsername " +
           "AND (:fromDate IS NULL OR t.trainingDate >= :fromDate) " +
           "AND (:toDate IS NULL OR t.trainingDate <= :toDate) " +
           "AND (:trainerName IS NULL OR CONCAT(t.trainer.user.firstName, ' ', t.trainer.user.lastName) LIKE %:trainerName%) " +
           "AND (:trainingTypeName IS NULL OR t.trainingType.trainingTypeName = :trainingTypeName)")
    List<Training> findTraineeTrainingsByCriteria(
            @Param("traineeUsername") String traineeUsername,
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate,
            @Param("trainerName") String trainerName,
            @Param("trainingTypeName") String trainingTypeName);

    @Query("SELECT t FROM Training t WHERE t.trainer.user.username = :trainerUsername " +
           "AND (:fromDate IS NULL OR t.trainingDate >= :fromDate) " +
           "AND (:toDate IS NULL OR t.trainingDate <= :toDate) " +
           "AND (:traineeName IS NULL OR CONCAT(t.trainee.user.firstName, ' ', t.trainee.user.lastName) LIKE %:traineeName%)")
    List<Training> findTrainerTrainingsByCriteria(
            @Param("trainerUsername") String trainerUsername,
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate,
            @Param("traineeName") String traineeName);
}
