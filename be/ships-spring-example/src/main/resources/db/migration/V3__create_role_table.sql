CREATE TABLE [roles] (
    [id] BIGINT PRIMARY KEY IDENTITY(1,1) NOT NULL,
    [name] VARCHAR(255) NOT NULL
);

ALTER TABLE [registered_user] ADD [role_id] BIGINT;

ALTER TABLE [registered_user] ADD CONSTRAINT [FK_roles_registered_user]
    FOREIGN KEY ([role_id]) REFERENCES [roles]([id]);

INSERT INTO roles (name) VALUES ( 'REGISTERED_USER');
INSERT INTO roles (name) VALUES ( 'ADMINISTRATOR');
INSERT INTO roles (name) VALUES ( 'ANONYMOUS_USER');
