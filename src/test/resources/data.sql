DROP TABLE IF EXISTS stock;

CREATE TABLE stock (
      id VARCHAR PRIMARY KEY,
      symbol VARCHAR NOT NULL,
      company_name VARCHAR NOT NULL,
      price FLOAT NOT NULL
);

INSERT INTO stock ("id", "symbol", "company_name", "price")
VALUES
    ('b2d13c5a-3df0-4673-b3e6-49244f395ac9', 'APPL1', 'Apple Inc.', 100.00),
    ('b2d13c5a-3df0-4673-b3e6-49244f395ad9', 'GOGL2', 'Alphabet Inc.', 200.00),
    ('b2d13c5a-3df0-4673-b3e6-49244f395ae9', 'MSFT3', 'Microsoft Corporation', 300.00),
    ('b2d13c5a-3df0-4673-b3e6-49244f395af9', 'AMZN4', 'Amazon.com Inc.', 400.00),
    ('b2d13c5a-3df0-4673-b3e6-49244f395a99', 'META5', 'Facebook Inc.', 500.00)
    ON CONFLICT (id) DO NOTHING;


DROP TABLE IF EXISTS users;

CREATE TABLE users (
                       email VARCHAR PRIMARY KEY,
                       username VARCHAR NOT NULL,
                       password VARCHAR NOT NULL,
                       firstName VARCHAR NOT NULL,
                       lastName VARCHAR,
                       birthDate DATE NOT NULL,
                       balance DECIMAL
);

INSERT INTO users (email, username, password, firstName, lastName, birthDate, balance)
VALUES
    ('joaopereira@hotmail.com', 'JoaoP', 'qwerty123', 'Joao', 'Pereira', '1988-07-05', 150.25),
    ('maria.rodrigues@yahoo.com', 'MariaRod', 'pass456', 'Maria', 'Rodrigues', '1992-12-15', 75.60),
    ('carlos.almeida@gmail.com', 'CarlosA', 'pass789', 'Carlos', 'Almeida', '1980-05-02', 500.00),
    ('ana.silva@gmail.com', 'AnaSilva', 'xyz789', 'Ana', 'Silva', '1995-03-21', 300.50),
    ('patricia.santos@hotmail.com', 'PatriciaS', 'pass123', 'Patricia', 'Santos', '1998-08-30', 250.75);