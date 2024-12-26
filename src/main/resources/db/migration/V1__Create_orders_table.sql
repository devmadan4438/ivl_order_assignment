CREATE TABLE IF NOT EXISTS sequence_generators (
                        id BIGINT PRIMARY KEY AUTO_INCREMENT,
                        sequence_name VARACHAR(255) NOT NULL,
                        year NUMBER NOT NULL,
                        sub_total FLOAT NOT NULL,
                        tax_amount FLOAT NOT NULL,
                        net_amount FLOAT NOT NULL,
                        created_by VARCHAR(255),
                        created_on DATETIME,
                        updated_by VARCHAR(255),
                        updated_on DATETIME,
                        deleted_by VARCHAR(255),
                        deleted_on DATETIME,
                        is_deleted BOOLEAN DEFAULT FALSE NOT NULL
);
