#---------
#CREATE DATABASE & TABLES
#---------

CREATE DATABASE core_database;
USE core_database;

# This setting is required so you can delete things without doing a WHERE on a unique key
SET SQL_SAFE_UPDATES = 0;

CREATE TABLE guild_logs
(
	guild_id char(18) primary key,
    log_channel_id char(18)
);

CREATE TABLE guild_permissions
(
	guild_id char(18),
    member_id char(18),
    permission varchar(1000)
);