# Moger API Readme
This is the documentation for the API code that provides various endpoints for managing parties, wallets, and transactions.

## Introduction
This API code is built using Python and the FastAPI framework. It allows you to manage parties, wallets, and transactions in a simple and efficient manner. The code is organized into different endpoints, each serving a specific purpose.

## Installation
To run the API code, follow these steps:

Clone the repository to your local machine.

Install the required dependencies by running 'pip install -r requirements.txt' in the project directory.

Make sure you have a MySQL database set up and update the database configuration in the 'app/config.py' file.

Start the API by running the following command in the project directory:

```
uvicorn app.main:app --reload
```

## Accessing Swagger Documentation
Once the API is running, you can access the Swagger documentation to explore the available endpoints and test them.

Open your web browser.
Go to 'http://localhost:8000/docs' to access the Swagger UI.
The Swagger UI provides an interactive interface to view and test the API endpoints. You can try out different requests and see the responses.
