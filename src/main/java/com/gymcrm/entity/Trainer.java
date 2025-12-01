package com.gymcrm.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "trainers")
public class Trainer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Include
    private Long id;

    @JoinColumn(name = "user_id",referencedColumnName = "id")
    @OneToOne(optional = true ,cascade = CascadeType.ALL)
    @ToString.Exclude
    private User user;

    @OneToMany(mappedBy = "trainer", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Training> trainings;

    @ManyToMany(mappedBy = "trainers")
    @ToString.Exclude
    private List<Trainee> trainees;



}
