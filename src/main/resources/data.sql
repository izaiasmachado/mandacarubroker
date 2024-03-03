INSERT INTO stock ("id", "symbol", "company_name", "price")
VALUES
    ('b2d13c5a-3df0-4673-b3e6-49244f395ac9', 'APPL1', 'Apple Inc.', 100.00),
    ('b2d13c5a-3df0-4673-b3e6-49244f395ad9', 'GOGL2', 'Alphabet Inc.', 200.00),
    ('b2d13c5a-3df0-4673-b3e6-49244f395ae9', 'MSFT3', 'Microsoft Corporation', 300.00),
    ('b2d13c5a-3df0-4673-b3e6-49244f395af9', 'AMZN4', 'Amazon.com Inc.', 400.00),
    ('b2d13c5a-3df0-4673-b3e6-49244f395a99', 'META5', 'Facebook Inc.', 500.00)
    ON CONFLICT (id) DO NOTHING;

INSERT INTO users (id, email, username, password, first_name, last_name, birth_date, balance)
VALUES
    ('b2d13c5a-3df0-4673-b3e7-49244f395ac8', 'joaopereira@hotmail.com', 'JoaoP', 'qwerty123', 'Joao', 'Pereira', '1988-07-05', 150.25),
    ('b2d13c5a-3df0-4673-b3e6-49245f395ac0', 'maria.rodrigues@yahoo.com', 'MariaRod', 'pass456', 'Maria', 'Rodrigues', '1992-12-15', 75.60),
    ('b2d13c5a-3df0-4673-b3e6-49244f394ac2', 'carlos.almeida@gmail.com', 'CarlosA', 'pass789', 'Carlos', 'Almeida', '1980-05-02', 500.00),
    ('b2d13c5a-7df0-4673-b3e6-49244f395ac1', 'ana.silva@gmail.com', 'AnaSilva', 'xyz789', 'Ana', 'Silva', '1995-03-21', 300.50),
    ('b2d13c5a-9df0-4673-b3e6-49244f395ac6', 'patricia.santos@hotmail.com', 'PatriciaS', 'pass123', 'Patricia', 'Santos', '1998-08-30', 250.75)
    ON CONFLICT (id) DO NOTHING;

INSERT INTO users (id, email, username, password, first_name, last_name, birth_date, balance, role)
VALUES
    ('b2d13c5a-3df0-4673-b3e6-49244f395ac7', 'admin@example.com', 'admin', '$2a$10$oUmo9dGdjnbdWeYlq7tsNuZo/r.pwI6T8JbEu2bp26Y5Zg7uzrKMy', 'Ademir', 'Ademilson', '2002-01-01', 777.7, 'ADMIN')
    ON CONFLICT (id) DO NOTHING;
