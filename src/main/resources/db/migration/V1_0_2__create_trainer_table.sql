-- DDL
CREATE TABLE trainers (
                          id SERIAL PRIMARY KEY,
                          specialization INT NOT NULL REFERENCES training_type(id),
                          user_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE
)
