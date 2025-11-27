package com.gymcrm.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.*;

import java.util.Date;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "trainees")
public class Trainee {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "date_of_birth")
    @Past
    private Date dateOfBirth;

    @Column(name = "address")
    private String address;

    @OneToOne(cascade = CascadeType.PERSIST,optional = false)
    @JoinColumn(name = "user_id",referencedColumnName = "id",nullable=false)
    @NotNull(message = "User cannot be null")
    private User user;

    @OneToMany(mappedBy = "trainee", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Training> trainings;

}
