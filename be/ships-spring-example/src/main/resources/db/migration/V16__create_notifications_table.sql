-- create table
CREATE TABLE notification (
    [id] BIGINT PRIMARY KEY IDENTITY(1,1) NOT NULL,
    [description] VARCHAR(255) NOT NULL,
    [registered_user_id] BIGINT NOT NULL,
);

-- registered user relations
ALTER TABLE notification ADD CONSTRAINT [FK_registered_user_notification]
    FOREIGN KEY ([registered_user_id]) REFERENCES [registered_user]([id]);