package com.gymcrm.entity;

import com.gymcrm.model.Training;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "training_types")
@Entity
public class TrainingType {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "training_type_name",unique = true, nullable = false)
    private String trainingTypeName;

    @OneToMany(mappedBy="trainingType",cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Training>trainings;
}
