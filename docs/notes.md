
# Challenges and Lessons Learned

## Docker Challenges

### Network and DNS
The initial Docker build failed because Docker could not
resolve a remote image download host. After checking DNS
connectivity and retrying, the image downloaded successfully.

### Port Conflict
Port 8080 was already occupied by another process.
We solved this by mapping host port 8081 to container
port 8080 in Docker Compose.

## Git and GitHub

GitHub rejected password authentication over HTTPS.
We used a fine-grained Personal Access Token with
Contents: Read and write permission.

## Lessons Learned

- Docker provides a consistent runtime environment.
- Docker Compose connects Spring Boot and MySQL containers.
- Docker volumes preserve database data.
- Git commits track changes throughout development.
- Environment variables keep credentials out of source code.
