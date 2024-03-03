CREATE TABLE stock_ownership (
    id VARCHAR PRIMARY KEY,
    shares INT NOT NULL,
    stock_id VARCHAR NOT NULL,
    user_id VARCHAR NOT NULL,
    FOREIGN KEY (stock_id) REFERENCES stock(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);