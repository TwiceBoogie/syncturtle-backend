<h1 align="center">Personal-Management-System (Backend)</h1>

## Documentation / Demo

- Documentation - available [here](#)
- Demo - click [here](#)
    - Login: admin
    - Password: admin

## Description

- [About](#about)
- [Reasoning](#reasoning)
- [Features](#features)
- [Technologies Used](#technologies_used)
- [Getting Started](#getting_started)
- [Usage](#usage)

<a name="about"></a>

## About

The Personal Management System is a versatile and user-friendly application designed to help users manage
their personal tasks, events, and more. The app offers less features than that of true psm but it shouldn't
be to hard to add more extensions to it. The backend follows a Domain Driven Design and each "service" follows
the same structure. I've tried to make it organize in case I decide to turn this into a microservice.

<a name="reasoning"></a>

## Reasoning

In order to further make my understanding of java stronger and learn spring boot along the way, I've decided to make my system
where it wouldn't be too hard to accomplish and features that I could have done on its own. This project allowed me to explore various technologies,
implement best practices, and again valuable experience in "software development" or atleast what I've read and learn in school. I hope this
demonstrates my proficiency in building feature-rich web applications, data management, and user authentication.

Demo GIF

<a name="features"></a>

## Features

- **Tasks Management**: Focuses on managing specific actions or activities users need to complete
- **Event Management**: explain
- **Goals Management**: Manages long-term objectives, aspirations, or achievements users want to accomplish
- **notes Management**: explain
- **Notifications Management**: explain
- **Passwords Management**: explain
- **Contacts Management**: explain
- **User Management (Admin)**: explain

<a name="technologies_used"></a>

## Technologies Used

- **Frontend**: [Next.JS]("")
- **Backend**: [Spring Boot]("")
- **Database**: [PostgreSQL]("")
- **Authentication**: [JJWT (Java JSON Web Tokens)]("")
- **Encryption**: [HashiCorp Vault]("")
- **Deployment**: My own web server

<a name="getting_started"></a>

## Getting Started

To run this project locally, follow these steps:

1. Clone the repository:

```shell
$ git clone https://github.com/your-username/personal-management-system.git
```

2. Navigate to the project directory:

```shell
$ cd path/to/personal-management-system
```

### Running Locally

3. Navigate to the following directories (backend & frontend) and run command:

```shell
// Backend
$ cd ./backend
$ mvn spring-boot:run

// Frontend
$ cd ./frontend
$ npm install
$ npm start dev
```

4. The backend will be running on 'http://localhost:8080' & the frontend on 'http://localhost:3000'.

### Running on Docker