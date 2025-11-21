CREATE TABLE trainers (
                          id SERIAL PRIMARY KEY,
                          specialization VARCHAR(100),
                          user_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE
);
