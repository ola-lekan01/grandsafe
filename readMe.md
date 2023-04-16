# grandsafe

Thullo is a digital platform that helps individuals and organizations manage their projects effectively. It provides a range of tools and features that make it easy to plan, track, and collaborate on various tasks and deliverables.<br>
***The API documentation is hosted [here]()***
<br>
## Technologies Used
- Java (Programming language)
- Springboot (Framework used to develop the APIs)
- Gradle (Dependency manager)
- postgreSQL (Database for data storage)
- JWT (Library for authentication)
- Railway (Hosting service)
- Spring Security (Framework used for security)

## Prerequisites

To build and run this project, you'll need:

- Java JDK 11 or later
- Spring Boot 3.0.5
- Gradle 7.6

## Getting Started

To get started with grandsafe, you will need to clone this repository to your local machine and set up the necessary dependencies.

### Installation

# Installation

1. Clone this repository to your local machine:

    ```bash
    git clone https://github.com/your-username/grandsafe.git
    ```

2. Create MySQL database

   ```bash
   mysql> create database grandsafe
   ```

3. Configure database username and password

     ```properties
       # src/main/resources/application.properties
      spring.datasource.url=jdbc:mysql://localhost:5432/grandsafe
      spring.datasource.username=<YOUR_DB_USERNAME>
      spring.datasource.password=<YOUR_DB_PASSWORD>
     ```

4. Set up the backend server:
   ```bash
      ./gradlew run
   ```


## Functional requirement

- User story: I can register a new account
- User story: I can log in
- User story: I can sign out
- User story: I can see my profile details
- User story: I can edit my details including: photo, name, bio, phone, email and password
- User story: I can upload a new photo or provide an image URL

## Non-Functional Requirements

The following non-functional requirements must be met:

- Security: The application must have robust security measures in place to protect user data and prevent unauthorized access.
- Availability: The application must be highly available, with minimal downtime and interruptions in service.
- Performance: The application must be optimized for low latency, with fast response times to user requests.

## Contributing

If you would like to contribute to THullo, please follow these guidelines:

1. Fork this repository to your own account
2. Create a branch for your feature: `git checkout -b feature/your-feature`
3. Commit your changes: `git commit -m "Add some feature"`
4. Push to your branch: `git push origin feature/your-feature`
5. Create a new Pull Request and describe your changes.
