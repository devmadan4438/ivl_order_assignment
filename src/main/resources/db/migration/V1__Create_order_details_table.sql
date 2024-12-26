CREATE TABLE IF NOT EXISTS order_details (
                               id BIGINT PRIMARY KEY AUTO_INCREMENT,
                               order_id BIGINT NOT NULL,
                               item_name VARCHAR(255) NOT NULL,
                               length FLOAT,
                               weight FLOAT,
                               breadth FLOAT,
                               height FLOAT,
                               uom VARCHAR(50),
                               size VARCHAR(50),
                               price FLOAT NOT NULL,
                               tax_per FLOAT NOT NULL,
                               tax_value FLOAT NOT NULL,
                               FOREIGN KEY (order_id) REFERENCES orders(id)
);
