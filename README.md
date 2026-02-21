# 🚀 Blackrock Hackathon: Expense Processing System

This is a high-performance Spring Boot application designed to handle complex financial transaction parsing, validation, and filtering, along with performance analytics, NPS return calculations, and index monitoring.

---
## Docker Link : 

https://hub.docker.com/r/singhsourabh074/expense-processing-system

## Video Link :


## 📋 Prerequisites

Before running the project locally, ensure you have the following installed:
* **JDK 17** or higher
* **Git**
* **Maven** (Optional, as the project includes the Maven Wrapper `mvnw`)
* **Docker** (Optional, for containerized execution)

---

## 🛠️ Local Setup & Execution

### 1. Clone the Repository
```bash
git clone [https://github.com/backbencher00/BlackrockHackathon.git](https://github.com/backbencher00/BlackrockHackathon.git)
cd BlackrockHackathon

```

### 2. Build the Project
#### For macOS or Linux:
```bash
./mvnw clean install
```

##### For Windows:
```bash
mvnw.cmd clean install
```



### 3. Run the application

#### For macOS or Linux:
```bash
./mvnw spring-boot:run
```

#### For Windows:
```
mvnw.cmd spring-boot:run
```


### 4. Docker setup
```bash
docker pull singhsourabh074/blackrock-hackathon:latest
```

```bash
docker run -p 5477:5477 singhsourabh074/blackrock-hackathon:latest
```
```bash
Docker Hub Link: https://hub.docker.com/r/singhsourabh074/blackrock-hackathon
```


### 5. API endpoint - for testing
```bash
1. Parse Transactions
curl --location 'http://localhost:5477/blackrock/challenge/v1/transactions:parse' \
--header 'Content-Type: application/json' \
--data '{
  "expenses": [
    { "date": "2023-10-12 20:15:30", "amount": 250 },
    { "date": "2023-02-28 15:49:20", "amount": 375 },
    { "date": "2023-07-01 21:59:00", "amount": 620 },
    { "date": "2023-12-17 08:09:45", "amount": 480 }
  ]
}'
``` 


2. Validate Transactions
```bash
curl --location 'http://localhost:5477/blackrock/challenge/v1/transactions:validator' \
--header 'Content-Type: application/json' \
--data '{
  "wage": 50000,
  "transactions": [
    { "date": "2023-01-15 10:30:00", "amount": 2000, "ceiling": 300, "remanent": 50 },
    { "date": "2023-03-20 14:45:00", "amount": 3500, "ceiling": 400, "remanent": 70 },
    { "date": "2023-06-10 09:15:00", "amount": 1500, "ceiling": 200, "remanent": 30 },
    { "date": "2023-07-10 09:15:00", "amount": -250, "ceiling": 200, "remanent": 30 },
    { "date": "2023-01-15 10:30:00", "amount": 2000, "ceiling": 300, "remanent": 50 }
  ]
}'
```


4. Filter Transactions
```bash
curl --location 'http://localhost:5477/blackrock/challenge/v1/transactions:filter' \
--header 'Content-Type: application/json' \
--data '{
  "q": [ { "fixed": 0, "start": "2023-07-01 00:00:00", "end": "2023-07-31 23:59:59" } ],
  "p": [ { "extra": 30, "start": "2023-10-01 00:00:00", "end": "2023-12-31 23:59:59" } ],
  "k": [ { "start": "2023-01-01 00:00:00", "end": "2023-12-31 23:59:59" } ],
  "wage": 50000,
  "transactions": [
    { "date": "2023-02-28 15:49:20", "amount": 375 },
    { "date": "2023-07-15 10:30:00", "amount": 620 },
    { "date": "2023-10-12 20:15:30", "amount": 250 },
    { "date": "2023-12-17 08:09:45", "amount": -480 }
  ]
}'
```



4. System Performance
```bash
curl --location 'http://localhost:5477/blackrock/challenge/v1/performance'
```

5. Returns: NPS

```bash
curl --location 'http://localhost:5477/blackrock/challenge/v1/returns:nps' \
--header 'Content-Type: application/json' \
--data '{
  "age": 29, "wage": 50000, "inflation": 5.5,
  "q": [ { "fixed": 0, "start": "2023-07-01 00:00:00", "end": "2023-07-31 23:59:59" } ],
  "p": [ { "extra": 25, "start": "2023-10-01 08:00:00", "end": "2023-12-31 19:59:59" } ],
  "k": [ { "start": "2023-03-01 00:00:00", "end": "2023-11-30 23:59:59" } ],
  "transactions": [
    { "date": "2023-02-28 15:49:20", "amount": 375 },
    { "date": "2023-07-01 21:59:00", "amount": 620 },
    { "date": "2023-12-17 08:09:45", "amount": 480 },
    { "date": "2023-12-17 08:09:45", "amount": -10 }
  ]
}'
```


6. Returns: Index
```bash
curl --location 'http://localhost:5477/blackrock/challenge/v1/returns:index' \
--header 'Content-Type: application/json' \
--data '{
  "age": 29, "wage": 50000, "inflation": 5.5,
  "q": [ { "fixed": 0, "start": "2023-07-01 00:00:00", "end": "2023-07-31 23:59:59" } ],
  "p": [ { "extra": 25, "start": "2023-10-01 08:00:00", "end": "2023-12-31 19:59:59" } ],
  "k": [ { "start": "2023-03-01 00:00:00", "end": "2023-11-30 23:59:59" } ],
  "transactions": [
    { "date": "2023-02-28 15:49:20", "amount": 375 },
    { "date": "2023-07-01 21:59:00", "amount": 620 },
    { "date": "2023-10-12 20:15:30", "amount": 250 },
    { "date": "2023-12-17 08:09:45", "amount": 480 },
    { "date": "2023-12-17 08:09:45", "amount": -10 }
  ]
}'
```







