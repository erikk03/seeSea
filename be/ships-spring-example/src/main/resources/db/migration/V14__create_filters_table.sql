-- create table
CREATE TABLE filters (
    [id] BIGINT PRIMARY KEY IDENTITY(1,1) NOT NULL,
    [filter_from] VARCHAR(255) NOT NULL DEFAULT 'All',
);

-- registered user relations
ALTER TABLE [registered_user] ADD [filters_id] BIGINT;

ALTER TABLE registered_user ADD CONSTRAINT [FK_filters_registered_user]
    FOREIGN KEY ([filters_id]) REFERENCES [filters]([id]);

-- vessel type relations
CREATE TABLE filters_vessel_type (
     [id] BIGINT PRIMARY KEY IDENTITY(1,1) NOT NULL,
     [filters_id] BIGINT NOT NULL,
     [vessel_type_id] BIGINT NOT NULL
);

ALTER TABLE filters_vessel_type ADD CONSTRAINT [FK_filters_filters_vessel_type]
    FOREIGN KEY ([filters_id]) REFERENCES [filters]([id]);

ALTER TABLE filters_vessel_type ADD CONSTRAINT [FK_vessel_type_filters_vessel_type]
    FOREIGN KEY ([vessel_type_id]) REFERENCES [vessel_type]([id]);

-- vessel status relations
CREATE TABLE filters_vessel_status (
    [id] BIGINT PRIMARY KEY IDENTITY(1,1) NOT NULL,
    [filters_id] BIGINT NOT NULL,
    [vessel_status_id] BIGINT NOT NULL
);

ALTER TABLE filters_vessel_status ADD CONSTRAINT [FK_filters_filters_vessel_status]
    FOREIGN KEY ([filters_id]) REFERENCES [filters]([id]);

ALTER TABLE filters_vessel_status ADD CONSTRAINT [FK_vessel_type_filters_vessel_status]
    FOREIGN KEY ([vessel_status_id]) REFERENCES [vessel_status]([id]);


