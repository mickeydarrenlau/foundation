--
create extension if not exists "uuid-ossp";
--
create extension if not exists timescaledb;
--
create schema if not exists heimdall;
--
create table if not exists heimdall.player_positions (
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
select create_hypertable('heimdall.player_positions', 'time', 'player', 4,  if_not_exists => TRUE);
--
create table if not exists heimdall.block_breaks (
    time timestamp not null,
    player uuid not null,
    world uuid not null,
    block text not null,
    x double precision not null,
    y double precision not null,
    z double precision not null,
    PRIMARY KEY (time, player, world)
);
--
select create_hypertable('heimdall.block_breaks', 'time', 'player', 4,  if_not_exists => TRUE);
--
create table if not exists heimdall.block_places (
    time timestamp not null,
    player uuid not null,
    world uuid not null,
    block text not null,
    x double precision not null,
    y double precision not null,
    z double precision not null,
    PRIMARY KEY (time, player, world)
);
--
select create_hypertable('heimdall.block_places', 'time', 'player', 4,  if_not_exists => TRUE);
--
create table if not exists heimdall.player_sessions (
    id uuid not null,
    player uuid not null,
    name text not null,
    "start" timestamp not null,
    "end" timestamp not null,
    primary key (id, player, start)
);
--
select create_hypertable('heimdall.player_sessions', 'start', 'player', 4,  if_not_exists => TRUE);
--
create table if not exists heimdall.world_changes (
    time timestamp not null,
    player uuid not null,
    from_world uuid not null,
    from_world_name text not null,
    to_world uuid not null,
    to_world_name text not null,
    primary key (time, player)
);
--
select create_hypertable('heimdall.world_changes', 'time', 'player', 4,  if_not_exists => TRUE);
--
create table if not exists heimdall.player_deaths (
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
select create_hypertable('heimdall.player_deaths', 'time', 'player', 4,  if_not_exists => TRUE);
--
create table if not exists heimdall.player_advancements (
    time timestamp not null,
    player uuid not null,
    world uuid not null,
    x double precision not null,
    y double precision not null,
    z double precision not null,
    pitch double precision not null,
    yaw double precision not null,
    advancement text null,
    primary key (time, player, advancement)
);
--
select create_hypertable('heimdall.player_advancements', 'time', 'player', 4,  if_not_exists => TRUE);
