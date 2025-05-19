DELETE FROM [vessel_history_data];

ALTER TABLE [vessel_history_data]
    ADD [datetime_created] DATETIME NOT NULL;