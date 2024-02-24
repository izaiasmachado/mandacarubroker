CREATE TABLE users (
                        username VARCHAR NOT NULL,
                        password VARCHAR NOT NULL,
                        email VARCHAR PRIMARY KEY,
                        firstName VARCHAR NOT NULL,
                        lastName VARCHAR,
                        birthDate DATE NOT NULL,
                        balance DECIMAL
)