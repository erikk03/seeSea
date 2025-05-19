ALTER TABLE [vessel]
    DROP CONSTRAINT [FK_vessel_status_vessel];

ALTER TABLE [vessel]
    DROP COLUMN [vessel_status_id];

EXEC sp_rename 'vessel_history_data.status', 'vessel_status_id', 'COLUMN';

ALTER TABLE [vessel_history_data]
    ALTER COLUMN [vessel_status_id] BIGINT;

ALTER TABLE vessel_history_data ADD CONSTRAINT [FK_vessel_status_vessel_history_data]
    FOREIGN KEY ([vessel_status_id]) REFERENCES [vessel_status]([id]);