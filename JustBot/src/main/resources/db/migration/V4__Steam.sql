CREATE TABLE IF NOT EXISTS dsbot_steam (
                                                  id  SERIAL PRIMARY KEY NOT NULL,
                                                  guild_id bigint REFERENCES dsbot_guild_beta(id),
                                                  steam_id bigint default 0,
                                                  discord_id bigint,
                                                  score bigint default 0
    );