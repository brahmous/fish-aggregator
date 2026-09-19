param (
  [string]$Command
)

$Command | docker exec -i postgres-test psql -U dev -d fish_db