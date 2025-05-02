CREATE TABLE [administrator] (
    [id] BIGINT PRIMARY KEY NOT NULL
);

ALTER TABLE [administrator] ADD CONSTRAINT [FK_administrator_registered_user]
    FOREIGN KEY ([id]) REFERENCES [registered_user]([id]);