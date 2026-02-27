-- V007: Add user profile fields (SQL Server)

ALTER TABLE users ADD phone NVARCHAR(30);
ALTER TABLE users ADD location NVARCHAR(100);
ALTER TABLE users ADD job_title NVARCHAR(100);
ALTER TABLE users ADD department NVARCHAR(100);
