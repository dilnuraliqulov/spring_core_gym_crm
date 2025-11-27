package com.gymcrm.entity;



import com.gymcrm.converter.CharToStringConverter;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @Column(name = "first_name",nullable = false,length = 50)
    @NotBlank(message= "Firstname can not be blank")
    private String firstName;

    @Column(name = "last_name",nullable = false,length = 50)
    @NotBlank(message = "Lastname can not be blank")
    private String lastName;

    @Setter(AccessLevel.NONE)
    @Column(name = "username",unique = true,nullable = false)
    @NotBlank(message = "Username can not be blank ")
    private String username;

    @Setter(AccessLevel.NONE)
    @Column(name = "password",nullable = false)
    @Convert(converter =  CharToStringConverter.class)
    @ToString.Exclude
    @NotNull(message = "Password can not be null")
    private char[] password;

    @Column(name = "active",nullable = false)
    private boolean isActive;

}
