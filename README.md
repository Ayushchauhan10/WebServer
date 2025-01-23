# MultiThreaded WebServer

A lightweight multithreaded web server built in Java, designed to handle multiple clients and provide static file serving and basic RESTful API functionality.

## Features
- **Multithreaded Server**: Processes multiple client requests concurrently.
- **Static File Serving**: Serves HTML, CSS, and other static files.
- **API Endpoints**: Supports GET, POST, PUT, and DELETE requests with data stored in a temporary text file.
- **Performance Tested**[using JMeter]: Handles up to 500 requests/second with > 90% success rate under load of 50k users.


### Testing Endpoints
- Use a tool like **Postman** to interact with the server:
  - **GET**: `http://localhost:8080/api`
  - **POST**: `http://localhost:8080/api?key=value`
  - **PUT**: `http://localhost:8080/api?key=newValue`
  - **DELETE**: `http://localhost:8080/api?key`

## Project Structure
```
Project Root
|— src/
   |— server/
   |   |— WebServer.java        # Main server class
   |   |— ClientHandler.java     # Handles client requests
   |   |— StaticFileHandler.java # Serves static files
   |   |— APIHandler.java        # Processes API requests
   |— files/
       |— index.html            # Sample static file
       |— style.css             # CSS for styling
       |— data.txt              # Temporary data storage for API
```

## Performance Metrics
- **Throughput**: 500 requests/second
- **Success Rate**: >90% under 50,000 requests
- **Average Response Time**: 400ms


