-- DDL

-- Rename tables to match entity names and alter ID columns from SERIAL (INT) to BIGINT

-- First rename tables that don't match entity names
ALTER TABLE IF EXISTS training_type RENAME TO training_types;
ALTER TABLE IF EXISTS training RENAME TO trainings;

-- First, drop foreign key constraints (handle both old and new constraint names)
ALTER TABLE trainees DROP CONSTRAINT IF EXISTS trainees_user_id_fkey;
ALTER TABLE trainers DROP CONSTRAINT IF EXISTS trainers_user_id_fkey;
ALTER TABLE trainers DROP CONSTRAINT IF EXISTS trainers_specialization_fkey;
ALTER TABLE trainings DROP CONSTRAINT IF EXISTS trainings_trainee_id_fkey;
ALTER TABLE trainings DROP CONSTRAINT IF EXISTS trainings_trainer_id_fkey;
ALTER TABLE trainings DROP CONSTRAINT IF EXISTS trainings_training_type_id_fkey;
ALTER TABLE trainings DROP CONSTRAINT IF EXISTS training_trainee_id_fkey;
ALTER TABLE trainings DROP CONSTRAINT IF EXISTS training_trainer_id_fkey;
ALTER TABLE trainings DROP CONSTRAINT IF EXISTS training_training_type_id_fkey;
ALTER TABLE trainee_trainers DROP CONSTRAINT IF EXISTS fk_trainee_trainers_trainee;
ALTER TABLE trainee_trainers DROP CONSTRAINT IF EXISTS fk_trainee_trainers_trainer;

-- Rename column in training_types if it exists as 'name' instead of 'training_type_name'
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'training_types' AND column_name = 'name') THEN
        ALTER TABLE training_types RENAME COLUMN name TO training_type_name;
    END IF;
END $$;

-- Rename column in trainings if it exists as 'training_duration' instead of 'duration_of_training'
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'trainings' AND column_name = 'training_duration') THEN
        ALTER TABLE trainings RENAME COLUMN training_duration TO duration_of_training;
    END IF;
END $$;

-- Rename column in users if it exists as 'is_active' instead of 'active'
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'users' AND column_name = 'is_active') THEN
        ALTER TABLE users RENAME COLUMN is_active TO active;
    END IF;
END $$;

-- Alter users table
ALTER TABLE users ALTER COLUMN id TYPE BIGINT USING id::BIGINT;
ALTER SEQUENCE IF EXISTS users_id_seq AS BIGINT;

-- Alter trainees table
ALTER TABLE trainees ALTER COLUMN id TYPE BIGINT USING id::BIGINT;
ALTER TABLE trainees ALTER COLUMN user_id TYPE BIGINT USING user_id::BIGINT;
ALTER SEQUENCE IF EXISTS trainees_id_seq AS BIGINT;

-- Alter training_types table
ALTER TABLE training_types ALTER COLUMN id TYPE BIGINT USING id::BIGINT;
ALTER SEQUENCE IF EXISTS training_types_id_seq AS BIGINT;
ALTER SEQUENCE IF EXISTS training_type_id_seq AS BIGINT;

-- Alter trainers table
ALTER TABLE trainers ALTER COLUMN id TYPE BIGINT USING id::BIGINT;
ALTER TABLE trainers ALTER COLUMN user_id TYPE BIGINT USING user_id::BIGINT;
ALTER TABLE trainers ALTER COLUMN specialization TYPE BIGINT USING specialization::BIGINT;
ALTER SEQUENCE IF EXISTS trainers_id_seq AS BIGINT;

-- Alter trainings table
ALTER TABLE trainings ALTER COLUMN id TYPE BIGINT USING id::BIGINT;
ALTER TABLE trainings ALTER COLUMN trainee_id TYPE BIGINT USING trainee_id::BIGINT;
ALTER TABLE trainings ALTER COLUMN trainer_id TYPE BIGINT USING trainer_id::BIGINT;
ALTER TABLE trainings ALTER COLUMN training_type_id TYPE BIGINT USING training_type_id::BIGINT;
ALTER SEQUENCE IF EXISTS trainings_id_seq AS BIGINT;
ALTER SEQUENCE IF EXISTS training_id_seq AS BIGINT;

-- Re-add foreign key constraints
ALTER TABLE trainees ADD CONSTRAINT trainees_user_id_fkey FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;
ALTER TABLE trainers ADD CONSTRAINT trainers_user_id_fkey FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;
ALTER TABLE trainers ADD CONSTRAINT trainers_specialization_fkey FOREIGN KEY (specialization) REFERENCES training_types(id);
ALTER TABLE trainings ADD CONSTRAINT trainings_trainee_id_fkey FOREIGN KEY (trainee_id) REFERENCES trainees(id) ON DELETE CASCADE;
ALTER TABLE trainings ADD CONSTRAINT trainings_trainer_id_fkey FOREIGN KEY (trainer_id) REFERENCES trainers(id) ON DELETE CASCADE;
ALTER TABLE trainings ADD CONSTRAINT trainings_training_type_id_fkey FOREIGN KEY (training_type_id) REFERENCES training_types(id);
ALTER TABLE trainee_trainers ADD CONSTRAINT fk_trainee_trainers_trainee FOREIGN KEY (trainee_id) REFERENCES trainees(id) ON DELETE CASCADE;
ALTER TABLE trainee_trainers ADD CONSTRAINT fk_trainee_trainers_trainer FOREIGN KEY (trainer_id) REFERENCES trainers(id) ON DELETE CASCADE;

