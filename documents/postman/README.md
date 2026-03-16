# Expense Splitter Postman Automation Pack

Run your Spring Boot API first.

From this folder:

```bash
npm install
BASE_URL=http://localhost:8080 bash run-newman.sh
```

This pack uses only the collection inside the `Postman/` folder. The runner injects `baseUrl` explicitly, stops on the first hard failure, and excludes the known broken API-gap requests from the automated flow.
