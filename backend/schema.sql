drop database if exists study_trek;

create database study_trek;

use study_trek;

create table user_info(
    userId VARCHAR(8) PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    passwordHash VARCHAR(255) NOT NULL,
    lastPasswordResetDate TIMESTAMP,
    firstName VARCHAR(255),
    lastName VARCHAR(255),
    profilePicUrl VARCHAR(255)

);

CREATE TABLE interests (
    interestId INT AUTO_INCREMENT PRIMARY KEY,
    userId VARCHAR(8),
    interest VARCHAR(255),
    FOREIGN KEY (userId) REFERENCES user_info(userId)
);

CREATE TABLE course_notes (
    noteId INT AUTO_INCREMENT PRIMARY KEY,
    userId VARCHAR(8),
    note VARCHAR(255),
    FOREIGN KEY (userId) REFERENCES user_info(userId)
);

CREATE TABLE registered_courses (
    courseId INT AUTO_INCREMENT PRIMARY KEY,
    userId VARCHAR(8),
    courseName VARCHAR(255),
    courseDescription VARCHAR(255),
    -- Add more fields as needed based on CourseDetails class
    FOREIGN KEY (userId) REFERENCES user_info(userId)
);

CREATE TABLE friends (
    friendId INT AUTO_INCREMENT PRIMARY KEY,
    userId VARCHAR(8),
    friendUserId VARCHAR(8),
    FOREIGN KEY (userId) REFERENCES user_info(userId)
);

CREATE TABLE roles (
    roleId INT AUTO_INCREMENT PRIMARY KEY,
    userId VARCHAR(8),
    role VARCHAR(255),
    FOREIGN KEY (userId) REFERENCES user_info(userId)
);

grant all privileges on study_trek.* to 'newuser'@'%';
flush privileges;