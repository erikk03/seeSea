CREATE TABLE [vessel_history_data] (
    [id] BIGINT PRIMARY KEY IDENTITY(1,1) NOT NULL,
    [vessel_mmsi] VARCHAR(255) NOT NULL,
    [status] INTEGER,
    [turn] FLOAT,
    [speed] FLOAT,
    [course] FLOAT,
    [heading] INTEGER,
    [longitude] FLOAT(53),
    [latitude] FLOAT(53),
    [timestamp] BIGINT NULL,
);

ALTER TABLE [vessel_history_data] ADD CONSTRAINT [FK_vessel_vessel_history_data]
    FOREIGN KEY ([vessel_mmsi]) REFERENCES [vessel]([mmsi]);