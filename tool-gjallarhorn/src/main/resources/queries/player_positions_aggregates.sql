WITH
    unique_player_ids AS (
        SELECT
            DISTINCT player
        FROM heimdall.player_sessions
    ),
    player_names AS (
        SELECT
               player,
               (
                   SELECT name
                   FROM heimdall.player_sessions
                   WHERE player = unique_player_ids.player
                   ORDER BY "end" DESC
                   LIMIT 1
               ) AS name
        FROM unique_player_ids
    ),
    unique_world_ids AS (
      SELECT
        DISTINCT to_world AS world
        FROM heimdall.world_changes
    ),
    world_names AS (
        SELECT
            world,
               (
                   SELECT to_world_name
                   FROM heimdall.world_changes
                   WHERE world = heimdall.world_changes.to_world
                   ORDER BY time DESC
                   LIMIT 1
                ) AS name
        FROM unique_world_ids
    ),
    player_calculated_positions AS (
        SELECT
               player,
               world,
               AVG(x) AS avg_x,
               AVG(y) AS avg_y,
               AVG(z) AS avg_z,
               MAX(x) AS max_x,
               MAX(y) AS max_y,
               MAX(z) AS max_z,
               MIN(x) AS min_x,
               MIN(y) AS min_y,
               MIN(z) AS min_z,
               COUNT(*) AS count,
               MODE() WITHIN GROUP (ORDER BY x) AS mode_x,
               MODE() WITHIN GROUP (ORDER BY y) AS mode_y,
               MODE() WITHIN GROUP (ORDER BY z) AS mode_z
        FROM heimdall.player_positions
        GROUP BY player, world
    )
SELECT
       player_names.name AS player_name,
       world_names.name AS world_name,
       player_calculated_positions.*
FROM player_calculated_positions
JOIN player_names
    ON player_names.player = player_calculated_positions.player
JOIN world_names
    ON world_names.world = player_calculated_positions.world
