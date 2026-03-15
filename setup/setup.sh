# before running this file run the docker daemon
docker run --name expense_splitter_database \
  -e POSTGRES_PASSWORD=mysecretpassword \
  -p 5432:5432 \
  -d postgres:18.3

docker exec -i expense_splitter_database psql -U postgres -c "CREATE DATABASE expense_splitter;"
docker exec -i expense_splitter_database psql -U postgres -d expense_splitter < schema.sql