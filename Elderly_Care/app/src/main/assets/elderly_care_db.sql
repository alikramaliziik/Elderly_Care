-- elderly_care_db.sql
-- Run this in Room on first app start (or pre-populate)

PRAGMA foreign_keys = ON;

-- Table: caregivers
CREATE TABLE caregivers (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    full_name TEXT NOT NULL,
    email TEXT NOT NULL UNIQUE,
    password_hash TEXT NOT NULL, -- In real app: use BCrypt
    phone TEXT NOT NULL,
    age INTEGER NOT NULL,
    gender TEXT CHECK(gender IN ('Male', 'Female', 'Other')) NOT NULL
);

-- Table: elderly
CREATE TABLE elderly (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    caregiver_id INTEGER NOT NULL,
    full_name TEXT NOT NULL,
    age INTEGER NOT NULL,
    gender TEXT CHECK(gender IN ('Male', 'Female', 'Other')) NOT NULL,
    phone TEXT,
    FOREIGN KEY (caregiver_id) REFERENCES caregivers(id) ON DELETE CASCADE
);

-- Pre-saved accounts
INSERT INTO caregivers (full_name, email, password_hash, phone, age, gender) VALUES
('Sarah Johnson', 'sarah@eldercare.com', 'password123', '+256 701 234 567', 38, 'Female'),
('Michael Okello', 'michael@eldercare.com', 'mike2025', '+256 772 345 678', 42, 'Male'),
('Amina Nakato', 'amina@eldercare.com', 'amina123', '+256 755 987 654', 35, 'Female');

-- Their elderly
INSERT INTO elderly (caregiver_id, full_name, age, gender, phone) VALUES
(1, 'Akram Kasozi', 72, 'Male', '+256 755 123 4567'),
(1, 'Fatuma Nansubuga', 68, 'Female', '+256 755 111 2222'),
(2, 'James Mukasa', 78, 'Male', '+256 772 999 8888'),
(3, 'Rose Namutebi', 70, 'Female', '+256 755 555 4444');
