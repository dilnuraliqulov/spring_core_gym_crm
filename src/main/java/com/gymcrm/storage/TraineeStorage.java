package com.gymcrm.storage;

import com.gymcrm.model.Trainee;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Getter
public class TraineeStorage {
    private final Map<Long, Trainee>traineeStorage = new HashMap<>();
}
