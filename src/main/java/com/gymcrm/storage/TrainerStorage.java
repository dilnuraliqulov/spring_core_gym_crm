package com.gymcrm.storage;

import com.gymcrm.model.Trainer;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Getter
public class TrainerStorage {
    private final Map<Long, Trainer>trainerStorage = new HashMap();

}
