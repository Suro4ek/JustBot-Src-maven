create table group_guild
(
    id  SERIAL PRIMARY KEY NOT NULL,
    guild_id bigint REFERENCES dsbot_guild_beta(id) on delete cascade,
    role_id     bigint  not null,
    name        varchar not null,
    def         boolean not null,
    permisisons text[]
);

