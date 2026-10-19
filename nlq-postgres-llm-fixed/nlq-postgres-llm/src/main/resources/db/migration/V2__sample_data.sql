INSERT INTO department (name, type)
SELECT 'HR', 'Corporate'
WHERE NOT EXISTS (SELECT 1 FROM department WHERE name = 'HR');

INSERT INTO department (name, type)
SELECT 'IT', 'Technology'
WHERE NOT EXISTS (SELECT 1 FROM department WHERE name = 'IT');

INSERT INTO department (name, type)
SELECT 'Finance', 'Corporate'
WHERE NOT EXISTS (SELECT 1 FROM department WHERE name = 'Finance');

INSERT INTO department (name, type)
SELECT 'Sales', 'Business'
WHERE NOT EXISTS (SELECT 1 FROM department WHERE name = 'Sales');

INSERT INTO employee (name, age, salary, fk_id_department)
SELECT 'Amit Sharma', 34, 1250000, d.id FROM department d WHERE d.name = 'HR'
  AND NOT EXISTS (SELECT 1 FROM employee WHERE name = 'Amit Sharma');

INSERT INTO employee (name, age, salary, fk_id_department)
SELECT 'Priya Mehta', 42, 980000, d.id FROM department d WHERE d.name = 'HR'
  AND NOT EXISTS (SELECT 1 FROM employee WHERE name = 'Priya Mehta');

INSERT INTO employee (name, age, salary, fk_id_department)
SELECT 'Rahul Verma', 29, 1500000, d.id FROM department d WHERE d.name = 'IT'
  AND NOT EXISTS (SELECT 1 FROM employee WHERE name = 'Rahul Verma');

INSERT INTO employee (name, age, salary, fk_id_department)
SELECT 'Neha Singh', 37, 1750000, d.id FROM department d WHERE d.name = 'IT'
  AND NOT EXISTS (SELECT 1 FROM employee WHERE name = 'Neha Singh');

INSERT INTO employee (name, age, salary, fk_id_department)
SELECT 'Vikram Patel', 51, 1100000, d.id FROM department d WHERE d.name = 'Finance'
  AND NOT EXISTS (SELECT 1 FROM employee WHERE name = 'Vikram Patel');

INSERT INTO employee (name, age, salary, fk_id_department)
SELECT 'Sneha Iyer', 31, 1350000, d.id FROM department d WHERE d.name = 'Sales'
  AND NOT EXISTS (SELECT 1 FROM employee WHERE name = 'Sneha Iyer');
