-- =============================================================================
-- 1. NHÓM HỆ THỐNG & NGƯỜI DÙNG (Giữ nguyên các bảng cốt lõi theo yêu cầu)
-- =============================================================================

IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'roles')
CREATE TABLE [roles] (
  [id] BIGINT IDENTITY(1,1) NOT NULL,
  [name] NVARCHAR(255) NOT NULL,
  CONSTRAINT [PK_roles] PRIMARY KEY ([id]),
  CONSTRAINT [UK_roles_name] UNIQUE ([name])
);

IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'users')
CREATE TABLE [users] (
  [id] BIGINT IDENTITY(1,1) NOT NULL,
  [username] NVARCHAR(50) NOT NULL,
  [password] NVARCHAR(255) NOT NULL,
  [email] NVARCHAR(100) NOT NULL,
  [first_name] NVARCHAR(50) NULL,
  [last_name] NVARCHAR(50) NULL,
  [phone_number] NVARCHAR(15) NULL,
  [address] NVARCHAR(255) NULL,
  [gender] NVARCHAR(20) NULL,
  [avatar_url] NVARCHAR(500) NULL,
  [verified] BIT NOT NULL CONSTRAINT [DF_users_verified] DEFAULT 0,
  [provider] NVARCHAR(20) CONSTRAINT [DF_users_provider] DEFAULT 'LOCAL',
  [google_id] NVARCHAR(255) NULL,
  [linked_at] DATETIME NULL,
  [status] NVARCHAR(20) CONSTRAINT [DF_users_status] DEFAULT 'ACTIVE',
  [registration_token] NVARCHAR(255) NULL,
  [reset_password_token] NVARCHAR(255) NULL,
  [token_expiry_date] DATETIME NULL,
  CONSTRAINT [PK_users] PRIMARY KEY ([id]),
  CONSTRAINT [UK_users_username] UNIQUE ([username]),
  CONSTRAINT [UK_users_email] UNIQUE ([email])
);

IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'user_roles')
CREATE TABLE [user_roles] (
  [user_id] BIGINT NOT NULL,
  [role_id] BIGINT NOT NULL,
  CONSTRAINT [PK_user_roles] PRIMARY KEY ([user_id], [role_id]),
  CONSTRAINT [FK_user_roles_role] FOREIGN KEY ([role_id]) REFERENCES [roles] ([id]),
  CONSTRAINT [FK_user_roles_user] FOREIGN KEY ([user_id]) REFERENCES [users] ([id])
);

IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'settings')
CREATE TABLE [settings] (
  [id] BIGINT IDENTITY(1,1) NOT NULL,
  [type] NVARCHAR(50) NOT NULL, -- Ví dụ: ROLE, SUBJECT_CATEGORY, POST_CATEGORY
  [value] NVARCHAR(255) NOT NULL,
  [display_order] INT NULL,
  [description] NVARCHAR(MAX) NULL,
  [status] NVARCHAR(20) CONSTRAINT [DF_settings_status] DEFAULT 'ACTIVE',
  CONSTRAINT [PK_settings] PRIMARY KEY ([id])
);

IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'audit_logs')
CREATE TABLE [audit_logs] (
  [id] BIGINT IDENTITY(1,1) NOT NULL,
  [action] NVARCHAR(255) NOT NULL,
  [entity_name] NVARCHAR(255) NOT NULL,
  [entity_id] NVARCHAR(255) NULL,
  [performed_by] NVARCHAR(255) NOT NULL,
  [timestamp] DATETIME NOT NULL CONSTRAINT [DF_audit_timestamp] DEFAULT GETDATE(),
  [details] NVARCHAR(MAX) NULL,
  [ip_address] NVARCHAR(45) NULL,
  CONSTRAINT [PK_audit_logs] PRIMARY KEY ([id])
);

-- =============================================================================
-- 2. NHÓM KHÓA HỌC & NỘI DUNG (SUBJECTS & DIMENSIONS)
-- =============================================================================

CREATE TABLE [subjects] (
  [id] BIGINT IDENTITY(1,1) NOT NULL,
  [title] NVARCHAR(255) NOT NULL,
  [thumbnail] NVARCHAR(255) NULL,
  [tagline] NVARCHAR(255) NULL,
  [description] NVARCHAR(MAX) NULL,
  [category_id] BIGINT NULL, -- Liên kết tới settings(type='SUBJECT_CATEGORY')
  [featured] BIT NOT NULL CONSTRAINT [DF_subjects_featured] DEFAULT 0,
  [status] NVARCHAR(20) CONSTRAINT [DF_subjects_status] DEFAULT 'UNPUBLISHED',
  [owner_id] BIGINT NULL, -- Chuyên gia (Expert) phụ trách
  [created_at] DATETIME CONSTRAINT [DF_subjects_created] DEFAULT GETDATE(),
  CONSTRAINT [PK_subjects] PRIMARY KEY ([id]),
  CONSTRAINT [FK_subjects_category] FOREIGN KEY ([category_id]) REFERENCES [settings] ([id]),
  CONSTRAINT [FK_subjects_owner] FOREIGN KEY ([owner_id]) REFERENCES [users] ([id])
);

CREATE TABLE [subject_dimensions] (
  [id] BIGINT IDENTITY(1,1) NOT NULL,
  [subject_id] BIGINT NOT NULL,
  [type_id] BIGINT NOT NULL, -- Ví dụ: Domain, Group
  [name] NVARCHAR(255) NOT NULL,
  [description] NVARCHAR(MAX) NULL,
  CONSTRAINT [PK_subject_dimensions] PRIMARY KEY ([id]),
  CONSTRAINT [FK_dimension_subject] FOREIGN KEY ([subject_id]) REFERENCES [subjects] ([id])
);

CREATE TABLE [price_packages] (
  [id] BIGINT IDENTITY(1,1) NOT NULL,
  [subject_id] BIGINT NOT NULL,
  [name] NVARCHAR(255) NOT NULL,
  [duration_months] INT NOT NULL,
  [list_price] DECIMAL(18,2) NOT NULL,
  [sale_price] DECIMAL(18,2) NOT NULL,
  [status] NVARCHAR(20) CONSTRAINT [DF_price_status] DEFAULT 'ACTIVE',
  [description] NVARCHAR(MAX) NULL,
  CONSTRAINT [PK_price_packages] PRIMARY KEY ([id]),
  CONSTRAINT [FK_price_subject] FOREIGN KEY ([subject_id]) REFERENCES [subjects] ([id])
);

-- =============================================================================
-- 3. NHÓM BÀI HỌC (LESSONS)
-- =============================================================================

CREATE TABLE [lessons] (
  [id] BIGINT IDENTITY(1,1) NOT NULL,
  [subject_id] BIGINT NOT NULL,
  [topic_id] BIGINT NULL, -- Phân cấp: Bài học thuộc về một Topic nào đó
  [title] NVARCHAR(255) NOT NULL,
  [type] NVARCHAR(50) NOT NULL, -- LESSON, QUIZ, SUBJECT_TOPIC
  [order_index] INT CONSTRAINT [DF_lesson_order] DEFAULT 0,
  [video_url] NVARCHAR(500) NULL,
  [html_content] NVARCHAR(MAX) NULL, -- Tương đương longtext
  [status] NVARCHAR(20) CONSTRAINT [DF_lesson_status] DEFAULT 'ACTIVE',
  CONSTRAINT [PK_lessons] PRIMARY KEY ([id]),
  CONSTRAINT [FK_lesson_subject] FOREIGN KEY ([subject_id]) REFERENCES [subjects] ([id]),
  CONSTRAINT [FK_lesson_topic] FOREIGN KEY ([topic_id]) REFERENCES [lessons] ([id])
);

-- =============================================================================
-- 4. NGÂN HÀNG CÂU HỎI & QUIZ (Dựa trên hình Quiz Review/Handle)
-- =============================================================================

CREATE TABLE [questions] (
  [id] BIGINT IDENTITY(1,1) NOT NULL,
  [subject_id] BIGINT NOT NULL,
  [dimension_id] BIGINT NULL,
  [lesson_id] BIGINT NULL,
  [level_id] BIGINT NOT NULL, -- Liên kết settings: Easy, Medium, Hard
  [content] NVARCHAR(MAX) NOT NULL,
  [explanation] NVARCHAR(MAX) NULL,
  [media_url] NVARCHAR(500) NULL,
  [status] NVARCHAR(20) CONSTRAINT [DF_question_status] DEFAULT 'ACTIVE',
  CONSTRAINT [PK_questions] PRIMARY KEY ([id]),
  CONSTRAINT [FK_question_subject] FOREIGN KEY ([subject_id]) REFERENCES [subjects] ([id]),
  CONSTRAINT [FK_question_dimension] FOREIGN KEY ([dimension_id]) REFERENCES [subject_dimensions] ([id]),
  CONSTRAINT [FK_question_lesson] FOREIGN KEY ([lesson_id]) REFERENCES [lessons] ([id])
);

CREATE TABLE [question_options] (
  [id] BIGINT IDENTITY(1,1) NOT NULL,
  [question_id] BIGINT NOT NULL,
  [content] NVARCHAR(MAX) NOT NULL,
  [is_correct] BIT NOT NULL CONSTRAINT [DF_option_correct] DEFAULT 0,
  CONSTRAINT [PK_question_options] PRIMARY KEY ([id]),
  CONSTRAINT [FK_option_question] FOREIGN KEY ([question_id]) REFERENCES [questions] ([id])
);

CREATE TABLE [quizzes] (
  [id] BIGINT IDENTITY(1,1) NOT NULL,
  [subject_id] BIGINT NOT NULL,
  [name] NVARCHAR(255) NOT NULL,
  [level_id] BIGINT NULL,
  [duration_minutes] INT NULL,
  [pass_rate] FLOAT NULL,
  [quiz_type] NVARCHAR(50) NULL, -- SIMULATION, TOPIC
  [number_of_questions] INT NULL,
  [description] NVARCHAR(MAX) NULL,
  [status] NVARCHAR(20) CONSTRAINT [DF_quiz_status] DEFAULT 'ACTIVE',
  CONSTRAINT [PK_quizzes] PRIMARY KEY ([id]),
  CONSTRAINT [FK_quiz_subject] FOREIGN KEY ([subject_id]) REFERENCES [subjects] ([id])
);

-- Lưu vết kết quả thi của người dùng
CREATE TABLE [quiz_attempts] (
  [id] BIGINT IDENTITY(1,1) NOT NULL,
  [user_id] BIGINT NOT NULL,
  [quiz_id] BIGINT NOT NULL,
  [score] FLOAT NULL,
  [status] NVARCHAR(20) CONSTRAINT [DF_attempt_status] DEFAULT 'SUBMITTED', -- IN_PROGRESS, SUBMITTED
  [started_at] DATETIME CONSTRAINT [DF_attempt_started] DEFAULT GETDATE(),
  [completed_at] DATETIME NULL,
  CONSTRAINT [PK_quiz_attempts] PRIMARY KEY ([id]),
  CONSTRAINT [FK_attempt_user] FOREIGN KEY ([user_id]) REFERENCES [users] ([id]),
  CONSTRAINT [FK_attempt_quiz] FOREIGN KEY ([quiz_id]) REFERENCES [quizzes] ([id])
);

-- Câu trả lời chi tiết cho từng câu trong lượt thi
CREATE TABLE [quiz_answers] (
  [id] BIGINT IDENTITY(1,1) NOT NULL,
  [attempt_id] BIGINT NOT NULL,
  [question_id] BIGINT NOT NULL,
  [chosen_option_id] BIGINT NULL,
  [is_marked] BIT NOT NULL CONSTRAINT [DF_answer_marked] DEFAULT 0, -- Tính năng Mark for Review
  CONSTRAINT [PK_quiz_answers] PRIMARY KEY ([id]),
  CONSTRAINT [FK_answer_attempt] FOREIGN KEY ([attempt_id]) REFERENCES [quiz_attempts] ([id])
);

-- =============================================================================
-- 5. NHÓM MARKETING & SALES (POSTS, SLIDERS, REGISTRATIONS)
-- =============================================================================

CREATE TABLE [posts] (
  [id] BIGINT IDENTITY(1,1) NOT NULL,
  [title] NVARCHAR(255) NOT NULL,
  [thumbnail] NVARCHAR(255) NULL,
  [category_id] BIGINT NULL, -- Liên kết tới settings: POST_CATEGORY
  [post_date] DATETIME CONSTRAINT [DF_post_date] DEFAULT GETDATE(),
  [content] NVARCHAR(MAX) NULL,
  [author_id] BIGINT NULL,
  [hot] BIT NOT NULL CONSTRAINT [DF_post_hot] DEFAULT 0,
  [status] NVARCHAR(20) CONSTRAINT [DF_post_status] DEFAULT 'ACTIVE',
  CONSTRAINT [PK_posts] PRIMARY KEY ([id]),
  CONSTRAINT [FK_post_category] FOREIGN KEY ([category_id]) REFERENCES [settings] ([id]),
  CONSTRAINT [FK_post_author] FOREIGN KEY ([author_id]) REFERENCES [users] ([id])
);

CREATE TABLE [sliders] (
  [id] BIGINT IDENTITY(1,1) NOT NULL,
  [title] NVARCHAR(255) NOT NULL,
  [image] NVARCHAR(255) NULL,
  [backlink] NVARCHAR(255) NULL,
  [status] NVARCHAR(20) CONSTRAINT [DF_slider_status] DEFAULT 'ACTIVE',
  [notes] NVARCHAR(MAX) NULL,
  CONSTRAINT [PK_sliders] PRIMARY KEY ([id])
);

CREATE TABLE [registrations] (
  [id] BIGINT IDENTITY(1,1) NOT NULL,
  [user_id] BIGINT NOT NULL,
  [subject_id] BIGINT NOT NULL,
  [package_id] BIGINT NOT NULL,
  [total_cost] DECIMAL(18,2) NOT NULL,
  [registration_time] DATETIME CONSTRAINT [DF_reg_time] DEFAULT GETDATE(),
  [status] NVARCHAR(50) CONSTRAINT [DF_reg_status] DEFAULT 'SUBMITTED', -- SUBMITTED, PAID, CANCELLED
  [valid_from] DATETIME NULL,
  [valid_to] DATETIME NULL,
  [last_updated_by] BIGINT NULL,
  [notes] NVARCHAR(MAX) NULL,
  CONSTRAINT [PK_registrations] PRIMARY KEY ([id]),
  CONSTRAINT [FK_reg_user] FOREIGN KEY ([user_id]) REFERENCES [users] ([id]),
  CONSTRAINT [FK_reg_subject] FOREIGN KEY ([subject_id]) REFERENCES [subjects] ([id]),
  CONSTRAINT [FK_reg_package] FOREIGN KEY ([package_id]) REFERENCES [price_packages] ([id])
);

-- =============================================================================
-- 6. DỮ LIỆU KHỞI TẠO (INIT DATA)
-- =============================================================================

-- Chèn Roles
INSERT INTO [roles] ([name]) VALUES ('ROLE_ADMIN'), ('ROLE_USER'), ('ROLE_EXPERT'), ('ROLE_MARKETING'), ('ROLE_SALE');

-- Chèn Settings
INSERT INTO [settings] ([type], [value], [display_order], [description]) VALUES 
('SUBJECT_CATEGORY', 'Programming', 1, 'Courses about coding and software development'),
('SUBJECT_CATEGORY', 'Design', 2, 'Courses about UI/UX and Graphic Design'),
('POST_CATEGORY', 'News', 1, 'System and industry news'),
('POST_CATEGORY', 'Tutorial', 2, 'How-to guides'),
('LEVEL', 'Easy', 1, 'Beginner level'),
('LEVEL', 'Medium', 2, 'Intermediate level'),
('LEVEL', 'Hard', 3, 'Advanced level');

-- Chèn Users (Password: 123456 - giả định đã được mã hóa nếu cần)
INSERT INTO [users] ([username], [password], [email], [first_name], [last_name], [verified], [status]) VALUES 
('admin', '$2a$10$Xm.7lG8V0D1i9Y3B7V0lE.5g1yR7XQvL1L/WkG2g.h9mG5jR6E5Gq', 'admin@onlinelearn.com', 'System', 'Admin', 1, 'ACTIVE'),
('expert1', '$2a$10$Xm.7lG8V0D1i9Y3B7V0lE.5g1yR7XQvL1L/WkG2g.h9mG5jR6E5Gq', 'expert1@onlinelearn.com', 'Java', 'Expert', 1, 'ACTIVE');

-- Gán quyền
INSERT INTO [user_roles] ([user_id], [role_id]) VALUES (1, 1), (2, 3);

-- Chèn Subjects
INSERT INTO [subjects] ([title], [thumbnail], [tagline], [category_id], [featured], [status], [owner_id]) VALUES 
('Java Web Development', 'java-thumb.jpg', 'Master Spring Boot and JPA', 1, 1, 'PUBLISHED', 2),
('UI/UX Fundamentals', 'design-thumb.jpg', 'Design beautiful interfaces', 2, 0, 'PUBLISHED', 2);

-- Chèn Price Packages
INSERT INTO [price_packages] ([subject_id], [name], [duration_months], [list_price], [sale_price], [status]) VALUES 
(1, 'Monthly Basic', 1, 150000.00, 120000.00, 'ACTIVE'),
(1, 'Yearly Pro', 12, 1200000.00, 1000000.00, 'ACTIVE');

-- Chèn Sliders
INSERT INTO [sliders] ([title], [image], [backlink], [status]) VALUES 
('New Courses 2026', 'slider1.jpg', '/courses', 'ACTIVE'),
('Expert Tips', 'slider2.jpg', '/blog', 'ACTIVE');