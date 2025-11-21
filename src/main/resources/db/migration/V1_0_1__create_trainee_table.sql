-- DDL
CREATE TABLE trainees (
                          id SERIAL PRIMARY KEY,
                          date_of_birth DATE,
                          address VARCHAR(255),
                          user_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE
);