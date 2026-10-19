CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE IF NOT EXISTS department (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(100),
    created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS employee (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    age INTEGER NOT NULL CHECK (age >= 0 AND age <= 120),
    salary NUMERIC(15,2) NOT NULL CHECK (salary >= 0),
    fk_id_department BIGINT NOT NULL REFERENCES department(id),
    created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_employee_department
    ON employee(fk_id_department);

CREATE INDEX IF NOT EXISTS idx_employee_age
    ON employee(age);

CREATE INDEX IF NOT EXISTS idx_employee_salary
    ON employee(salary);

CREATE INDEX IF NOT EXISTS idx_department_name
    ON department(name);
