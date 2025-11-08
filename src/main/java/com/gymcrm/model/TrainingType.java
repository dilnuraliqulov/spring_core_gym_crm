package com.gymcrm.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(of="id")
public class TrainingType implements IdAccessor {
private Long id;
private String trainingTypeName;
}
