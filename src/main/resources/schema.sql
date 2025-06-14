CREATE TABLE `user`(
                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                     username VARCHAR(50) NOT NULL,
                     password VARCHAR(100) NOT NULL,
                     enabled BOOLEAN NOT NULL DEFAULT true,
                     account_non_expired BOOLEAN NOT NULL DEFAULT true,
                     account_non_locked BOOLEAN NOT NULL DEFAULT true,
                     credentials_non_expired BOOLEAN NOT NULL DEFAULT true
);

CREATE TABLE authority(
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          role VARCHAR(50) NOT NULL
);

CREATE TABLE user_authority(
                               user_id BIGINT,
                               authority_id BIGINT,
                               FOREIGN KEY (user_id) REFERENCES `user`(id),
                               FOREIGN KEY (authority_id) REFERENCES authority(id),
                               PRIMARY KEY (user_id, authority_id)
);

CREATE TABLE lawyer(
                       lawyer_id INT AUTO_INCREMENT PRIMARY KEY,
                       first_name VARCHAR(20) NOT NULL,
                       last_name VARCHAR(20) NOT NULL,
                       hire_date DATE,
                       lawyer_type VARCHAR(15),
                       hourly_rate INT,
                       commission FLOAT,
                       lawyer_address VARCHAR(100)
);

CREATE TABLE `client`(
                         client_id INT AUTO_INCREMENT PRIMARY KEY,
                         first_name VARCHAR(20) NOT NULL,
                         last_name VARCHAR(20) NOT NULL,
                         client_address VARCHAR(100)
);

CREATE TABLE invoice(
                        invoice_id INT AUTO_INCREMENT PRIMARY KEY,
                        client_id INT,
                        amount INT,
                        due_date DATE,
                        FOREIGN KEY(client_id) REFERENCES `client`(client_id)
);

CREATE TABLE lawsuit(
                        lawsuit_id INT AUTO_INCREMENT PRIMARY KEY,
                        client_id INT NOT NULL,
                        reason VARCHAR(50) NOT NULL,
                        opposing_party VARCHAR(50) NOT NULL,
                        lawsuit_data MEDIUMBLOB,
                        FOREIGN KEY(client_id) REFERENCES `client`(client_id)
);

CREATE TABLE hearing(
                        hearing_id INT AUTO_INCREMENT PRIMARY KEY,
                        lawsuit_id INT,
                        date_time TIMESTAMP,
                        appointment_address VARCHAR(100),
                        FOREIGN KEY(lawsuit_id) REFERENCES lawsuit(lawsuit_id)
);

CREATE TABLE lawyer_lawsuit(
                               id INT AUTO_INCREMENT PRIMARY KEY,
                               lawyer_id INT,
                               lawsuit_id INT,
                               hours_worked INT,
                               FOREIGN KEY(lawyer_id) REFERENCES lawyer(lawyer_id),
                               FOREIGN KEY(lawsuit_id) REFERENCES lawsuit(lawsuit_id)
);
