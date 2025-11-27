package com.gymcrm.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "trainings")
public class Training {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainee_id")
    @ToString.Exclude
    @NotNull(message = "Trainee cannot null")
    private Trainee trainee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainer_id")
    @ToString.Exclude
    @NotNull(message = "Trainer cannot null")
    private Trainer trainer;

    @Column(name = "training_name", nullable = false)
    @NotBlank(message = "Training cannot be blank")
    private String trainingName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "training_type_id")
    @ToString.Exclude
    @NotNull(message = "Training type cannot be null")
    private TrainingType trainingType;

    @Column(name = "training_date", nullable = false)
    @Future
    private Date trainingDate;

    @Column(name = "duration_of_training",nullable = false)
    private Integer durationOfTraining; //in minutes
}

