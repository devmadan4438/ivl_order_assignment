CREATE TABLE IF NOT EXISTS sequence_generators (
                                                   id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                                   sequence_name VARCHAR(255) NOT NULL,
    year INT NOT NULL,
    counter INT DEFAULT 1 NOT NULL
    );
