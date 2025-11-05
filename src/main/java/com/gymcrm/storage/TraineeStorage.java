package com.gymcrm.storage;

import com.gymcrm.model.Trainee;
import org.springframework.stereotype.Component;

@Component
public class TraineeStorage extends TypedStorage<Trainee> {
    @Override
    public Class<Trainee> getType() {
        return Trainee.class;
    }
}
