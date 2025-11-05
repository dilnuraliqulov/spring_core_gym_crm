package com.gymcrm.storage;

import com.gymcrm.model.Training;
import org.springframework.stereotype.Component;

@Component
public class TrainingStorage extends TypedStorage<Training> {
    @Override
    public Class<Training> getType() {
        return Training.class;
    }
}
