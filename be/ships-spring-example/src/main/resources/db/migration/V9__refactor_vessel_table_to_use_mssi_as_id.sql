ALTER TABLE [vessel]
    DROP CONSTRAINT [FK_vessel_type_vessel];

DROP TABLE [vessel];

CREATE TABLE [vessel] (
    [mmsi] VARCHAR(255) PRIMARY KEY NOT NULL,
);

ALTER TABLE [vessel] ADD [vessel_type_id] BIGINT;

ALTER TABLE [vessel] ADD CONSTRAINT [FK_vessel_type_vessel]
    FOREIGN KEY ([vessel_type_id]) REFERENCES [vessel_type]([id]);
