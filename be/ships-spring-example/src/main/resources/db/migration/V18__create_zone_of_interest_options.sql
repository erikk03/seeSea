-- create table
CREATE TABLE zone_of_interest_options (
    [id] BIGINT PRIMARY KEY IDENTITY(1,1) NOT NULL,
    [max_speed] FLOAT,
    [enters_zone] BIT,
    [exits_zone] BIT,
);

-- registered user relations
ALTER TABLE [registered_user] ADD [zone_of_interest_options_id] BIGINT;

ALTER TABLE registered_user ADD CONSTRAINT [FK_zone_of_interest_options_registered_user]
    FOREIGN KEY ([zone_of_interest_options_id]) REFERENCES [zone_of_interest_options]([id]);