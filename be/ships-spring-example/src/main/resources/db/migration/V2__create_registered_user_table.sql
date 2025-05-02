CREATE TABLE [registered_user] (
    [id] BIGINT PRIMARY KEY IDENTITY(1,1) NOT NULL,
    [username] VARCHAR(255) NOT NULL,
    [email] VARCHAR(255) NOT NULL,
    [password] VARCHAR(500) NOT NULL,
    [user_type_id] bigint NOT NULL,
);