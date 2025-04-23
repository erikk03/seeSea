CREATE TABLE [vessel] (
    [id] bigint PRIMARY KEY IDENTITY(1,1) NOT NULL,
    [mmsi] varchar(255) NOT NULL UNIQUE,
);