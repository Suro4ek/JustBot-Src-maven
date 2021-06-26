create table dsbot_guild_beta
(
    id  SERIAL PRIMARY KEY NOT NULL,
    guildid           bigint,
    prefix            char              not null,
    volume            integer           not null,
    musicid           bigint,
    nsfwid            bigint,
    blockcommads      text[],
    lang              integer default 0,
    category_id       bigint  default 0 not null,
    create_channel_id bigint  default 0 not null,
    statsid           bigint  default 0,
    stats             boolean default false,
    animeid           bigint
);