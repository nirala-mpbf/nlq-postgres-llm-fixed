-- OPTIONAL: use this when employee/department already exist.
-- Review it before running against production.
-- The application expects primary keys named id and employee.fk_id_department -> department.id.

CREATE EXTENSION IF NOT EXISTS vector;

DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'employee' AND column_name = 'slary'
    ) AND NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'employee' AND column_name = 'salary'
    ) THEN
        ALTER TABLE employee RENAME COLUMN slary TO salary;
    END IF;
END $$;

ALTER TABLE department ADD COLUMN IF NOT EXISTS id BIGSERIAL;
ALTER TABLE employee ADD COLUMN IF NOT EXISTS id BIGSERIAL;
ALTER TABLE department ADD COLUMN IF NOT EXISTS created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE department ADD COLUMN IF NOT EXISTS modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE employee ADD COLUMN IF NOT EXISTS created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE employee ADD COLUMN IF NOT EXISTS modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conrelid = 'department'::regclass AND contype = 'p'
    ) THEN
        ALTER TABLE department ADD CONSTRAINT department_pkey PRIMARY KEY (id);
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conrelid = 'employee'::regclass AND contype = 'p'
    ) THEN
        ALTER TABLE employee ADD CONSTRAINT employee_pkey PRIMARY KEY (id);
    END IF;
END $$;

CREATE INDEX IF NOT EXISTS idx_employee_department ON employee(fk_id_department);
CREATE INDEX IF NOT EXISTS idx_employee_age ON employee(age);
CREATE INDEX IF NOT EXISTS idx_employee_salary ON employee(salary);
CREATE INDEX IF NOT EXISTS idx_department_name ON department(name);

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conrelid = 'employee'::regclass
          AND contype = 'f'
          AND confrelid = 'department'::regclass
    ) THEN
        ALTER TABLE employee
            ADD CONSTRAINT fk_employee_department
            FOREIGN KEY (fk_id_department) REFERENCES department(id);
    END IF;
END $$;
