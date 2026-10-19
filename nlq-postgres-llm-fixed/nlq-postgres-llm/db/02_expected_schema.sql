-- Expected application schema.
-- If your tables already exist, compare these columns and add the missing id/constraints/indexes.

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
    age INTEGER NOT NULL,
    salary NUMERIC(15,2) NOT NULL,
    fk_id_department BIGINT NOT NULL REFERENCES department(id),
    created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_employee_department ON employee(fk_id_department);
CREATE INDEX IF NOT EXISTS idx_employee_age ON employee(age);
CREATE INDEX IF NOT EXISTS idx_employee_salary ON employee(salary);
CREATE INDEX IF NOT EXISTS idx_department_name ON department(name);
