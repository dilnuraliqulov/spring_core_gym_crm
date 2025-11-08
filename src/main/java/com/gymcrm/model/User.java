package com.gymcrm.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(of="id")
public class User implements IdAccessor {
   private Long id;
   private String firstName;
   private String lastName;
   private String username;
   private String password;
   private boolean isActive;

}
