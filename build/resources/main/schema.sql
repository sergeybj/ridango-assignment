DROP TABLE IF EXISTS payment CASCADE;
DROP TABLE IF EXISTS account CASCADE;

DROP ALL OBJECTS DELETE FILES;

CREATE TABLE account(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    balance NUMERIC(18, 2) NOT NULL,
    transaction_in_progress BOOLEAN NOT NULL
);

CREATE TABLE payment(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sender_account_id BIGINT NOT NULL,
    receiver_account_id BIGINT NOT NULL,
    amount NUMERIC(18, 2) NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    FOREIGN KEY (sender_account_id) REFERENCES account(id),
    FOREIGN KEY (receiver_account_id) REFERENCES account(id)
--   started_processing BOOLEAN NOT NULL,
--   finished_processing BOOLEAN NOT NULL

);


INSERT INTO account (id,name,balance,transaction_in_progress)
VALUES
(100221,'Cyrus Mcbride','35.37',false),
(100222,'Karen Cervantes','62.13',false),
(100223,'Sean England','43.29',false),
(100224,'Hanae Patel','13.98',false),
(100225,'Constance Stafford','41.52',false);
