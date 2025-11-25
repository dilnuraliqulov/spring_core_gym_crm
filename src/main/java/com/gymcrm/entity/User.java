package com.gymcrm.entity;



import com.gymcrm.converter.CharToStringConverter;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;


@Entity
@Getter
@Setter
@ToString
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "first_name",nullable = false)
    private String firstName;

    @Column(name = "last_name",nullable = false)
    private String lastName;

    @Column(name = "email",unique = true,nullable = false)
    private String email;

    @Setter(AccessLevel.NONE)
    @Column(name = "username",unique = true,nullable = false)
    private String username;

    @Setter(AccessLevel.NONE)
    @Column(unique = true,nullable = false)
    @Convert(converter =  CharToStringConverter.class)
    @ToString.Exclude
    private char[] password;

    @Column(name = "active",nullable = false)
    private boolean isActive;

}
