CREATE TABLE [migration] (
    [id] BIGINT PRIMARY KEY IDENTITY(1,1) NOT NULL,
    [description] VARCHAR(255) NOT NULL UNIQUE,
    [done] BIT NOT NULL
);

INSERT INTO migration (description, done)
VALUES ('Load vessel types from CSV', 0);
