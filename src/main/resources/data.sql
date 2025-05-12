-- LAWYERS
INSERT INTO Lawyer (first_name, last_name, hire_date, lawyer_type, hourly_rate, commission, lawyer_address) VALUES
                                                                                                                ('John', 'Smith', '2015-06-15', 'Criminal', 300, 0.05, '123 Court St, New York, NY'),
                                                                                                                ('Alice', 'Wright', '2018-03-22', 'Civil', 250, 0.04, '456 Main St, Los Angeles, CA'),
                                                                                                                ('Mark', 'Johnson', '2020-01-05', 'Corporate', 400, 0.06, '789 Elm St, Chicago, IL'),
                                                                                                                ('Sara', 'Connor', '2019-11-11', 'Family', 200, 0.03, '321 Oak St, Dallas, TX'),
                                                                                                                ('Tom', 'Lee', '2021-07-07', 'Tax', 350, 0.05, '654 Pine St, Miami, FL'),
                                                                                                                ('Laura', 'King', '2016-09-30', 'Civil', 240, 0.04, '852 Maple St, Denver, CO'),
                                                                                                                ('Brian', 'Moore', '2014-02-02', 'Criminal', 310, 0.05, '147 Cedar St, Atlanta, GA'),
                                                                                                                ('Emily', 'Davis', '2017-12-19', 'Corporate', 390, 0.06, '963 Walnut St, Seattle, WA'),
                                                                                                                ('Kevin', 'Brown', '2022-04-01', 'Family', 210, 0.03, '753 Birch St, Boston, MA'),
                                                                                                                ('Nancy', 'Adams', '2013-05-20', 'Civil', 230, 0.04, '369 Chestnut St, Houston, TX'),
                                                                                                                ('Greg', 'Taylor', '2010-08-15', 'Criminal', 320, 0.05, '111 Pineapple St, Tampa, FL');

-- CLIENTS
INSERT INTO Client (first_name, last_name, client_address) VALUES
                                                               ('David', 'Anderson', '101 First St, Austin, TX'),
                                                               ('Rachel', 'Bennett', '202 Second St, San Diego, CA'),
                                                               ('George', 'Clark', '303 Third St, Phoenix, AZ'),
                                                               ('Linda', 'Diaz', '404 Fourth St, Orlando, FL'),
                                                               ('Paul', 'Evans', '505 Fifth St, Columbus, OH'),
                                                               ('Diana', 'Foster', '606 Sixth St, Portland, OR'),
                                                               ('Henry', 'Garcia', '707 Seventh St, Nashville, TN'),
                                                               ('Susan', 'Hall', '808 Eighth St, Charlotte, NC'),
                                                               ('James', 'Irwin', '909 Ninth St, Detroit, MI'),
                                                               ('Karen', 'Jones', '111 Tenth St, San Francisco, CA'),
                                                               ('Peter', 'Knight', '222 Eleventh St, Salt Lake City, UT');

-- INVOICES
INSERT INTO Invoice (client_id, amount, due_date) VALUES
                                                      (1, 1200, '2025-06-01'),
                                                      (2, 1800, '2025-06-10'),
                                                      (3, 950, '2025-06-15'),
                                                      (4, 1300, '2025-07-01'),
                                                      (5, 2000, '2025-07-10'),
                                                      (6, 1600, '2025-07-15'),
                                                      (7, 850, '2025-08-01'),
                                                      (8, 1750, '2025-08-10'),
                                                      (9, 2200, '2025-08-20'),
                                                      (10, 1050, '2025-09-01'),
                                                      (11, 1950, '2025-09-10'),
                                                      (1, 2300, '2025-09-15'),
                                                      (2, 1400, '2025-09-20'),
                                                      (3, 1800, '2025-10-01'),
                                                      (4, 1600, '2025-10-05'),
                                                      (5, 2100, '2025-10-10'),
                                                      (6, 2500, '2025-10-15'),
                                                      (7, 1900, '2025-11-01'),
                                                      (8, 2200, '2025-11-05'),
                                                      (9, 1350, '2025-11-10'),
                                                      (10, 1600, '2025-11-15'),
                                                      (11, 1450, '2025-12-01'),
                                                      (1, 1750, '2025-12-05'),
                                                      (2, 2100, '2025-12-10'),
                                                      (3, 1950, '2025-12-15'),
                                                      (4, 2300, '2025-12-20'),
                                                      (5, 1200, '2025-12-25');

-- LAWSUITS
INSERT INTO Lawsuit (client_id, reason, opposing_party) VALUES
                                                            (1, 'Breach of Contract', 'ABC Corp'),
                                                            (2, 'Divorce Settlement', 'John Bennett'),
                                                            (3, 'Fraud Allegation', 'XYZ Inc.'),
                                                            (4, 'Personal Injury', 'Acme Insurance'),
                                                            (5, 'Property Dispute', 'Neighbor'),
                                                            (6, 'Tax Evasion', 'IRS'),
                                                            (7, 'Intellectual Property', 'Design Co.'),
                                                            (8, 'Workplace Harassment', 'Employer Inc.'),
                                                            (9, 'Medical Malpractice', 'Dr. Lee'),
                                                            (10, 'Wrongful Termination', 'BigCorp LLC'),
                                                            (11, 'Defamation', 'Media House');

-- HEARINGS
INSERT INTO Hearing (lawsuit_id, date_time, appointment_address) VALUES
                                                                     (1, '2025-06-20 10:00:00', 'Courtroom 101, NY'),
                                                                     (2, '2025-06-25 11:00:00', 'Courtroom 12, CA'),
                                                                     (3, '2025-07-05 09:30:00', 'Courtroom B, IL'),
                                                                     (4, '2025-07-10 14:00:00', 'Courtroom C, TX'),
                                                                     (5, '2025-07-15 13:00:00', 'Courtroom D, FL'),
                                                                     (6, '2025-07-20 15:00:00', 'Courtroom A, CO'),
                                                                     (7, '2025-07-25 10:30:00', 'Courtroom 5, GA'),
                                                                     (8, '2025-07-30 11:30:00', 'Courtroom 3, WA'),
                                                                     (9, '2025-08-01 10:00:00', 'Courtroom Z, MA'),
                                                                     (10, '2025-08-05 09:00:00', 'Courtroom X, TX'),
                                                                     (11, '2025-08-10 16:00:00', 'Courtroom Q, FL');

-- LAWYER_LAWSUIT
INSERT INTO Lawyer_Lawsuit (lawyer_id, lawsuit_id, hours_worked) VALUES
                                                                     (1, 1, 10),
                                                                     (2, 2, 15),
                                                                     (3, 3, 12),
                                                                     (4, 4, 8),
                                                                     (5, 5, 20),
                                                                     (6, 6, 18),
                                                                     (7, 7, 14),
                                                                     (8, 8, 11),
                                                                     (9, 9, 7),
                                                                     (10, 10, 16),
                                                                     (11, 11, 13);
