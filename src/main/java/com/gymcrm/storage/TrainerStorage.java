package com.gymcrm.storage;

import com.gymcrm.model.Trainer;
import org.springframework.stereotype.Component;

@Component
public class TrainerStorage extends TypedStorage<Trainer> {
    @Override
    public Class<Trainer> getType() {
        return Trainer.class;
    }
}
