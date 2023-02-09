create extension if not exists "uuid-ossp";
--
create extension if not exists timescaledb;
--
create schema if not exists heimdall;
--
create table if not exists player_positions (
    time timestamp not null,
    player uuid not null,
    world uuid not null,
    x double precision not null,
    y double precision not null,
    z double precision not null,
    pitch double precision not null,
    yaw double precision not null,
    PRIMARY KEY (time, player, world)
);
--
select create_hypertable('player_positions', 'time', 'player', 4,  if_not_exists => TRUE);
--
alter table player_positions set (
    timescaledb.compress,
    timescaledb.compress_segmentby = 'player,world',
    timescaledb.compress_orderby = 'time'
);
--
select add_compression_policy('player_positions', interval '3 days', if_not_exists => true);
--
create table if not exists block_changes (
    time timestamp not null,
    inc int not null,
    player uuid null,
    world uuid not null,
    x double precision not null,
    y double precision not null,
    z double precision not null,
    pitch double precision not null,
    yaw double precision not null,
    block text not null,
    data text not null,
    cause text not null,
    PRIMARY KEY (time, inc)
);
--
select create_hypertable('block_changes', 'time', 'inc', 4,  if_not_exists => TRUE);
--
create table if not exists player_sessions (
    id uuid not null,
    player uuid not null,
    name text not null,
    "start" timestamp not null,
    "end" timestamp not null,
    primary key (id, player, start)
);
--
select create_hypertable('player_sessions', 'start', 'player', 4,  if_not_exists => TRUE);
--
create table if not exists world_changes (
    time timestamp not null,
    player uuid not null,
    from_world uuid not null,
    from_world_name text not null,
    to_world uuid not null,
    to_world_name text not null,
    primary key (time, player)
);
--
select create_hypertable('world_changes', 'time', 'player', 4,  if_not_exists => TRUE);
--
create table if not exists player_deaths (
    time timestamp not null,
    player uuid not null,
    world uuid not null,
    x double precision not null,
    y double precision not null,
    z double precision not null,
    pitch double precision not null,
    yaw double precision not null,
    experience double precision not null,
    message text null,
    primary key (time, player)
);
--
select create_hypertable('player_deaths', 'time', 'player', 4,  if_not_exists => TRUE);
--
create table if not exists player_advancements (
    time timestamp not null,
    player uuid not null,
    world uuid not null,
    x double precision not null,
    y double precision not null,
    z double precision not null,
    pitch double precision not null,
    yaw double precision not null,
    advancement text not null,
    primary key (time, player, advancement)
);
--
select create_hypertable('player_advancements', 'time', 'player', 4,  if_not_exists => TRUE);
--
create table if not exists entity_kills (
    time timestamp not null,
    player uuid not null,
    entity uuid not null,
    world uuid not null,
    x double precision not null,
    y double precision not null,
    z double precision not null,
    pitch double precision not null,
    yaw double precision not null,
    entity_type text not null,
    primary key (time, entity, player)
);
--
select create_hypertable('entity_kills', 'time', 'player', 4,  if_not_exists => TRUE);
--
create or replace view player_names as
    with unique_player_ids as (
        select distinct player
        from player_sessions
    )
    select player, (
        select name
        from player_sessions
        where player = unique_player_ids.player
        order by "end" desc
        limit 1
    ) as name
    from unique_player_ids;
--
