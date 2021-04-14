CREATE TABLE IF NOT EXISTS discord_ban (
    id  SERIAL PRIMARY KEY NOT NULL,
    banned bigint not null,
    guild_id bigint REFERENCES dsbot_guild_beta(id),
    banned_by bigint not null,
    cause TEXT,
    time TIMESTAMP,
    expires_on TIMESTAMP
);