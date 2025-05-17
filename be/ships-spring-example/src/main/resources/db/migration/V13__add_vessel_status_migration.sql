-- create table
CREATE TABLE [vessel_status] (
    [id] BIGINT PRIMARY KEY NOT NULL,
    [name] VARCHAR(255) NOT NULL
);

ALTER TABLE [vessel] ADD [vessel_status_id] BIGINT;

ALTER TABLE [vessel] ADD CONSTRAINT [FK_vessel_status_vessel]
    FOREIGN KEY ([vessel_status_id]) REFERENCES [vessel_status]([id]);

-- for migration
INSERT INTO migration (description, done)
    VALUES ('Load vessel status from CSV', 0);

