CREATE TABLE IF NOT EXISTS dsbot_reactionrole (
                                                id  SERIAL PRIMARY KEY NOT NULL,
                                                guild_id bigint REFERENCES dsbot_guild_beta(id),
                                                message_id bigint,
                                                roles text[]
);