-- DDL
-- Create trainee_trainers join table for many-to-many relationship
CREATE TABLE trainee_trainers (
    trainee_id BIGINT NOT NULL,
    trainer_id BIGINT NOT NULL,
    PRIMARY KEY (trainee_id, trainer_id),
    CONSTRAINT fk_trainee_trainers_trainee FOREIGN KEY (trainee_id) REFERENCES trainees(id) ON DELETE CASCADE,
    CONSTRAINT fk_trainee_trainers_trainer FOREIGN KEY (trainer_id) REFERENCES trainers(id) ON DELETE CASCADE
);

-- Create indexes for better query performance
CREATE INDEX idx_trainee_trainers_trainee_id ON trainee_trainers(trainee_id);
CREATE INDEX idx_trainee_trainers_trainer_id ON trainee_trainers(trainer_id);

