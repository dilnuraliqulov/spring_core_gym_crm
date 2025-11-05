package com.gymcrm.storage;

import com.gymcrm.model.TrainingType;
import org.springframework.stereotype.Component;

@Component
public class TrainingTypeStorage extends TypedStorage<TrainingType> {
    @Override
    public Class<TrainingType> getType() {
        return TrainingType.class;
    }
}
