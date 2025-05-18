CREATE TABLE [registered_user_vessel] (
    [id] BIGINT PRIMARY KEY IDENTITY(1,1) NOT NULL,
    [registered_user_id] BIGINT NOT NULL,
    [vessel_mmsi] VARCHAR(255) NOT NULL
);

ALTER TABLE [registered_user_vessel] ADD CONSTRAINT [FK_registered_user_vessel_registered_user]
    FOREIGN KEY ([registered_user_id]) REFERENCES [registered_user]([id]);

ALTER TABLE [registered_user_vessel] ADD CONSTRAINT [FK_registered_user_vessel_vessel]
    FOREIGN KEY ([vessel_mmsi]) REFERENCES [vessel]([mmsi]);