-- create table
CREATE TABLE filters (
    [id] BIGINT PRIMARY KEY IDENTITY(1,1) NOT NULL,
    [registered_user_id] BIGINT NOT NULL UNIQUE ,
    [filter_from] VARCHAR(255) NOT NULL DEFAULT 'All',
    [vessel_type_id] BIGINT,
    [vessel_status_id] BIGINT
);

ALTER TABLE [registered_user] ADD [filters_id] BIGINT UNIQUE;

ALTER TABLE registered_user ADD CONSTRAINT [FK_filters_registered_user]
    FOREIGN KEY ([filters_id]) REFERENCES [filters]([id]);

ALTER TABLE filters ADD CONSTRAINT [FK_registered_user_filters]
    FOREIGN KEY ([registered_user_id]) REFERENCES [registered_user]([id]);

ALTER TABLE filters ADD CONSTRAINT [FK_vessel_type_filters]
    FOREIGN KEY ([vessel_type_id]) REFERENCES [vessel_type]([id]);

ALTER TABLE filters ADD CONSTRAINT [FK_vessel_status_filters]
    FOREIGN KEY ([vessel_status_id]) REFERENCES [vessel_status]([id]);

