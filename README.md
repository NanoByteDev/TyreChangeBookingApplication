# Tyre Change Booking System

This project is a Spring Boot application that provides a backend API for a tyre change booking system. It allows users to view available workshops, check available time slots, and book appointments for tyre changes. This application was initially developed as part of a take-home coding assignment for a job application, demonstrating skills in Spring Boot, API design, and integration with external services.

## Features

- List all available tyre change workshops
- Fetch available time slots for a specific workshop
- Book appointments for tyre changes
- Support for different vehicle types
- Integration with external APIs for different workshops
- Link to the external APIs this is made to work with https://github.com/Surmus/tire-change-workshop?tab=readme-ov-file

## Technologies Used

- Java 17
- Spring Boot 3.3.3
- Maven
- Vue.js (for frontend)

## API Endpoints

- GET `/api/workshops`: Fetch all available workshops
- GET `/api/workshops/{name}/available-times`: Get available time slots for a specific workshop
- POST `/api/bookings`: Create a new booking

## Backend

Ensure you have Java 17 and Maven installed on your system.
1. Navigate to the backend directory
   ```
   cd Backend\tyrechange
   ```
3. Run the application: 
    ```
    mvn spring-boot:run
    ```

## Frontend

The frontend is built with Vue.js. To run the frontend:

1. Navigate to the frontend directory:
   ```
   cd Frontend\tyre-booking-app
   ```
2. Install dependencies:
   ```
   npm install
   ```
3. Run the development server:
   ```
   npm run dev
   ```

## Configuration

Workshop configurations are stored in the `workshops.yml` file. You can add or modify workshop details in this file.

## Testing
The tests are somewhat incomplete but there are some.
Run the tests using:
```
mvn test
```
