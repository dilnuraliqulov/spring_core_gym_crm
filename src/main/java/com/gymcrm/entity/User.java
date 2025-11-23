package com.gymcrm.entity;



import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "users")
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(unique = true,nullable = false)
    private String email;

    @Setter(AccessLevel.NONE)
    @Column(unique = true,nullable = false)
    private String username;

    @Setter(AccessLevel.NONE)
    @Column(unique = true,nullable = false)
    private String password;

    @Column(nullable = false)
    private Boolean isActive;

}
