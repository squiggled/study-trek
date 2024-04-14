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
    FOREIGN KEY (courseId) REFERENCES registered_courses(courseId) ON DELETE CASCADE
);

CREATE TABLE user_progress (
    progressId INT AUTO_INCREMENT PRIMARY KEY,
    userId VARCHAR(8),
    curriculumId INT,
    completed BOOLEAN,
    FOREIGN KEY (userId) REFERENCES user_info(userId),
    FOREIGN KEY (curriculumId) REFERENCES curriculum(curriculumId) ON DELETE CASCADE
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

CREATE TABLE notifications (
    notificationId INT AUTO_INCREMENT PRIMARY KEY,
    userId VARCHAR(8),
    type VARCHAR(255), -- For example, 'FRIEND_REQUEST'
    message VARCHAR(255),
    relatedId VARCHAR(255), -- ID of the related entity, like a friend request ID
    readStatus BOOLEAN DEFAULT FALSE,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (userId) REFERENCES user_info(userId)
);

CREATE TABLE friend_requests (
    requestId INT AUTO_INCREMENT PRIMARY KEY,
    senderId VARCHAR(8),
    receiverId VARCHAR(8),
    status ENUM('PENDING', 'ACCEPTED', 'REJECTED') NOT NULL,
    sentTimestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    responseTimestamp TIMESTAMP,
    FOREIGN KEY (senderId) REFERENCES user_info(userId),
    FOREIGN KEY (receiverId) REFERENCES user_info(userId)
);

CREATE TABLE subscriptions (
    subscriptionId INT AUTO_INCREMENT PRIMARY KEY,
    userId VARCHAR(8),
    subscriptionType VARCHAR(255), -- For example, 'PREMIUM', 'BASIC'
    startDate DATE,
    endDate DATE,
    status ENUM('ACTIVE', 'CANCELLED', 'EXPIRED') NOT NULL,
    autoRenew BOOLEAN DEFAULT TRUE, 
    lastPaymentDate TIMESTAMP,
    nextPaymentDate TIMESTAMP,
    FOREIGN KEY (userId) REFERENCES user_info(userId)
);

CREATE TABLE calendar_events (
    calendarId INT AUTO_INCREMENT PRIMARY KEY,
    userId VARCHAR(8),
    title VARCHAR(255) NOT NULL,
    text TEXT,
    selectedHour INT,
    date DATE,
    CONSTRAINT fk_user
        FOREIGN KEY (userId) 
        REFERENCES user_info(userId)
);

CREATE TABLE telegram_chats (
    userId VARCHAR(8) PRIMARY KEY,        
    chatId BIGINT NOT NULL,           
    last_interaction TIMESTAMP,
    FOREIGN KEY (userId) REFERENCES user_info(userId)
);

ALTER TABLE user_info
ADD COLUMN telegram_user_id BIGINT;

ALTER TABLE user_info
ADD COLUMN linkCode VARCHAR(255);

grant all privileges on study_trek.* to 'newuser'@'%';
flush privileges;