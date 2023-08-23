# grandsafe

grandsafe is a electronic digital platform that helps individuals manage their funds more 
effectively. It provides a range of tools and features that make it easy to save, spend and
manage funds from their grandsafe account.

***The API documentation is hosted [here]()***

## Technologies Used
- Java (Programming language)
- Springboot (Framework used to develop the APIs)
- Gradle (Dependency manager)
- postgreSQL (Database for data storage)
- JWT (Library for authentication)
- Render (Hosting service)
- Spring Security (Framework used for security)

## Prerequisites

To build and run this project, you'll need:

- Java JDK 11 or later
- Spring Boot 3.0.5
- Gradle 7.6 or later

## Getting Started

To get started with grandsafe, you will need to clone this repository to your local machine and set up the necessary dependencies.


# Installation

1. Clone this repository to your local machine:

    ```bash
    git clone https://github.com/your-username/grandsafe.git
    ```

2. Create PostgreSQL database

   ```bash
   psql> create database grandsafe
   ```

3. Configure database username and password

     ```properties
       # src/main/resources/application.properties
      spring.datasource.url=jdbc:postgresql://localhost:5432/grandsafe
      spring.datasource.username=<YOUR_DB_USERNAME>
      spring.datasource.password=<YOUR_DB_PASSWORD>
     ```

4. Set up the backend server:
   ```bash
      ./gradlew run
   ```


## Functional requirement
- User story: I can register a new grandsafe account
- User story: I can log in to my grandsafe account
- User story: I can see my grandsafe profile details 
- User story: I can edit my grandsafe profile details including: photo, name, phone, email and password
- User story: I have an account number that serves as my grandsafe wallet 
- User story: I can add my bank cards to my grandsafe account
- User story: I can deposit money to my grandsafe wallet from my cards
- User story: I can create saving plan to auto-debit my card to my grandsafe wallet
- User story: I can get all my saving plan that I am currently subscribed
- User story: I can make withdrawals from my grandsafe wallet to my bank account
- User story: I can check my grandsafe wallet account balance
- User story: I can use my grandsafe wallet balance to pay some basic utility bills

## Non-Functional Requirements

The following non-functional requirements must be met:

- Security: The application must have robust security measures in place to protect user data and prevent unauthorized access.
- Availability: The application must be highly available, with minimal downtime and interruptions in service.
- Performance: The application must be optimized for low latency, with fast response times to user requests.

## Contributing

If you would like to contribute to grandsafe, please follow these guidelines:

1. Fork this repository to your own account
2. Create a branch for your feature: `git checkout -b feature/your-feature`
3. Commit your changes: `git commit -m "Add some feature"`
4. Push to your branch: `git push origin feature/your-feature`
5. Create a new Pull Request and describe your changes.
