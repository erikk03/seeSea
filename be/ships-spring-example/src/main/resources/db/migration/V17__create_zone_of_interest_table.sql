-- create table
CREATE TABLE zone_of_interest (
    [id] BIGINT PRIMARY KEY IDENTITY(1,1) NOT NULL,
    [radius] FLOAT(53) NOT NULL,
    [center_point_latitude] FLOAT(53) NOT NULL,
    [center_point_longitude] FLOAT(53) NOT NULL,
);

-- registered user relations
ALTER TABLE [registered_user] ADD [zone_of_interest_id] BIGINT;

ALTER TABLE registered_user ADD CONSTRAINT [FK_zone_of_interest_registered_user]
    FOREIGN KEY ([zone_of_interest_id]) REFERENCES [zone_of_interest]([id]);