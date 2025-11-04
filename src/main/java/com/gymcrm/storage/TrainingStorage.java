package com.gymcrm.storage;

import com.gymcrm.model.Training;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Getter
public class TrainingStorage {
    private final Map<Long, Training>traningStorage = new HashMap<>();
}

