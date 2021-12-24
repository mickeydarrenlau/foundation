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
