CREATE TABLE [vessel_type] (
    [id] BIGINT PRIMARY KEY IDENTITY(1,1) NOT NULL,
    [name] VARCHAR(255) NOT NULL
);

ALTER TABLE [vessel] ADD [vessel_type_id] BIGINT;

ALTER TABLE [vessel] ADD CONSTRAINT [FK_vessel_type_vessel]
    FOREIGN KEY ([vessel_type_id]) REFERENCES [vessel_type]([id]);

INSERT INTO vessel_type (name) VALUES ( 'cargo');
INSERT INTO vessel_type (name) VALUES ( 'shipping');
INSERT INTO vessel_type (name) VALUES ( 'passenger');