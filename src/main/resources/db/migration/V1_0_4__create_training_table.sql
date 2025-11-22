-- DDL
CREATE TABLE training (
                          id SERIAL PRIMARY KEY,
                          trainee_id INT NOT NULL REFERENCES trainees(id) ON DELETE CASCADE,
                          trainer_id INT NOT NULL REFERENCES trainers(id) ON DELETE CASCADE,
                          training_type_id INT NOT NULL REFERENCES training_type(id),
                          training_name VARCHAR(100) NOT NULL,
                          training_date DATE NOT NULL,
                          training_duration INT NOT NULL
);
