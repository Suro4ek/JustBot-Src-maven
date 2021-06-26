CREATE TABLE IF NOT EXISTS dsbot_privatka (
    id  SERIAL PRIMARY KEY NOT NULL,
    guild_id bigint REFERENCES dsbot_guild_beta(id) on delete cascade,
    channel_id bigint not null,
    owner_id   bigint,
    text_id    bigint default 0
);