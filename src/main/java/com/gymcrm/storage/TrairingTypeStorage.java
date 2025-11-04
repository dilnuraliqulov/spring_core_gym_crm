package com.gymcrm.storage;

import com.gymcrm.model.TrainingType;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Getter
public class TrairingTypeStorage {
    private final Map<Long, TrainingType>trainingTypeStorage = new HashMap<>();
}
