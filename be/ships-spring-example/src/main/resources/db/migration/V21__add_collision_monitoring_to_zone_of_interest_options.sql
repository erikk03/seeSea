ALTER TABLE [zone_of_interest_options]
    ADD [collision_monitoring] BIT DEFAULT 0;
GO

UPDATE [zone_of_interest_options]
SET [collision_monitoring] = 0
WHERE [collision_monitoring] IS NULL;