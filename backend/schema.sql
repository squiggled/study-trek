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


CREATE TABLE registered_courses (
    courseId INT AUTO_INCREMENT PRIMARY KEY,
    userId VARCHAR(8),
    platform VARCHAR(255),
    platformId VARCHAR(255),
    title VARCHAR(255),
    headline VARCHAR(255),
    imageUrl VARCHAR(255),
    urlToCourse VARCHAR(255),
    isPaid BOOLEAN,
    price VARCHAR(255),
    instructor VARCHAR(255),
    isEnrolled BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (userId) REFERENCES user_info(userId)
);

CREATE TABLE curriculum (
    curriculumId INT AUTO_INCREMENT PRIMARY KEY,
    courseId INT, 
    lectureNumber INT,
    title VARCHAR(255),
    FOREIGN KEY (courseId) REFERENCES registered_courses(courseId)
);

CREATE TABLE user_progress (
    progressId INT AUTO_INCREMENT PRIMARY KEY,
    userId VARCHAR(8),
    curriculumId INT,
    completed BOOLEAN,
    FOREIGN KEY (userId) REFERENCES user_info(userId),
    FOREIGN KEY (curriculumId) REFERENCES curriculum(curriculumId)
);

CREATE TABLE course_notes (
    noteId INT AUTO_INCREMENT PRIMARY KEY,
    courseId INT,
    userId VARCHAR(8),
    note VARCHAR(255),
    FOREIGN KEY (userId) REFERENCES user_info(userId),
    FOREIGN KEY (courseId) REFERENCES registered_courses(courseId)
);


CREATE TABLE friends (
    userId VARCHAR(8),
    friendUserId VARCHAR(8),
    PRIMARY KEY (userId, friendUserId),
    FOREIGN KEY (userId) REFERENCES user_info(userId),
    FOREIGN KEY (friendUserId) REFERENCES user_info(userId)
);

CREATE TABLE roles (
    roleId INT AUTO_INCREMENT PRIMARY KEY,
    userId VARCHAR(8),
    role VARCHAR(255),
    FOREIGN KEY (userId) REFERENCES user_info(userId)
);

grant all privileges on study_trek.* to 'newuser'@'%';
flush privileges;