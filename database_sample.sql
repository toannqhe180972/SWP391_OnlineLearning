-- Database: online_learning_db
-- ------------------------------------------------------

SET NAMES utf8mb4;
SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO';
SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0;

CREATE DATABASE IF NOT EXISTS `online_learning_db` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `online_learning_db`;

-- 1. Table `roles`
CREATE TABLE `roles` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_roles_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. Table `users`
CREATE TABLE `users` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `email` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `first_name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `last_name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `phone_number` varchar(15) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `address` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `gender` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `avatar_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `verified` bit(1) NOT NULL DEFAULT b'0',
  `provider` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'LOCAL',
  `google_id` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `linked_at` datetime DEFAULT NULL,
  `status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'ACTIVE',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_users_username` (`username`),
  UNIQUE KEY `UK_users_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. Table `user_roles` (Many-to-Many Bridge)
CREATE TABLE `user_roles` (
  `user_id` bigint(20) NOT NULL,
  `role_id` bigint(20) NOT NULL,
  PRIMARY KEY (`user_id`,`role_id`),
  KEY `FK_user_roles_role` (`role_id`),
  CONSTRAINT `FK_user_roles_role` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`),
  CONSTRAINT `FK_user_roles_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. Table `settings`
CREATE TABLE `settings` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `type` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `value` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `display_order` int(11) DEFAULT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'ACTIVE',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. Table `audit_logs`
CREATE TABLE `audit_logs` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `action` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `entity_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `entity_id` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `performed_by` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `timestamp` datetime NOT NULL,
  `details` text COLLATE utf8mb4_unicode_ci,
  `ip_address` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 6. Table `subjects`
CREATE TABLE `subjects` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `title` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `thumbnail` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tagline` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `featured` bit(1) NOT NULL DEFAULT b'0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 7. Table `posts`
CREATE TABLE `posts` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `title` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `thumbnail` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `post_date` datetime DEFAULT NULL,
  `content` text COLLATE utf8mb4_unicode_ci,
  `hot` bit(1) NOT NULL DEFAULT b'0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 8. Table `sliders`
CREATE TABLE `sliders` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `title` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `image` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `backlink` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------
-- SAMPLE DATA
-- ------------------------------------------------------

-- Roles
INSERT INTO `roles` (`id`, `name`) VALUES (1, 'ROLE_ADMIN'), (2, 'ROLE_USER');

-- Users (Passwords are 'admin123' and 'user123' BCrypted)
INSERT INTO `users` (`id`, `username`, `password`, `email`, `first_name`, `last_name`, `verified`, `status`) VALUES 
(1, 'admin', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.z.UBvG2', 'admin@onlinelearning.com', 'System', 'Admin', 1, 'ACTIVE'),
(2, 'user', '$2a$10$PnS/mKqDskfO598YIOn3p.yvY6.C2vS8RGeI7R9uE7.iX6.0fV9uG', 'user@onlinelearning.com', 'Regular', 'User', 1, 'ACTIVE');

-- Assign Roles
INSERT INTO `user_roles` (`user_id`, `role_id`) VALUES (1, 1), (2, 2);

-- Settings
INSERT INTO `settings` (`type`, `value`, `display_order`, `description`, `status`) VALUES 
('SUBJECT_CATEGORY', 'Programming', 1, 'Programming subjects', 'ACTIVE'),
('SUBJECT_CATEGORY', 'Design', 2, 'Design subjects', 'ACTIVE'),
('POST_TYPE', 'News', 1, 'News posts', 'ACTIVE');

-- Sliders
INSERT INTO `sliders` (`title`, `image`, `backlink`, `status`) VALUES 
('Welcome', '/images/slider1.jpg', '/subjects', 'Active');

-- Subjects
INSERT INTO `subjects` (`title`, `thumbnail`, `tagline`, `description`, `featured`) VALUES 
('Java', '/images/java.jpg', 'Learn Java', 'Expert content', 1);

-- Posts
INSERT INTO `posts` (`title`, `thumbnail`, `post_date`, `content`, `hot`) VALUES 
('News', '/images/news1.jpg', NOW(), 'Content', 1);

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
SET SQL_NOTES=@OLD_SQL_NOTES;
