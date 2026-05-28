CREATE DATABASE social_network;

CREATE TABLE groups (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    headman_id INTEGER NULL
);

CREATE TABLE students (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE student_groups (
    student_id INTEGER NOT NULL REFERENCES students(id) ON DELETE CASCADE,
    group_id INTEGER NOT NULL REFERENCES groups(id) ON DELETE CASCADE,
    PRIMARY KEY (student_id, group_id)
);

CREATE TABLE friendships (
    id SERIAL PRIMARY KEY,
    student1_id INTEGER NOT NULL REFERENCES students(id) ON DELETE CASCADE,
    student2_id INTEGER NOT NULL REFERENCES students(id) ON DELETE CASCADE,
    CHECK (student1_id < student2_id),
    UNIQUE (student1_id, student2_id)
);

ALTER TABLE groups ADD CONSTRAINT fk_headman FOREIGN KEY (headman_id) REFERENCES students(id) ON DELETE SET NULL;
