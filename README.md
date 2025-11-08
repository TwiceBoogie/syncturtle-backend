<a id="readme-top"></a>

<br/>
<div align="center">
  <img src="docs/images/white-horizontal-logo-with-text.png" alt="Logo">
  <h3 align="center">Backend - Work In Progress</h3>
  <p align="center">
    SyncTurtle - Open-source, extensible personal management
    <br />
    <a href="https://github.com/TwiceBoogie/syncturtle-backend"><strong>Explore the docs »</strong></a>
    <br />
    <br />
    <a href="https://github.com/TwiceBoogie/syncturtle-backend">View Demo</a>
    &middot;
    <a href="https://github.com/TwiceBoogie/syncturtle-backend/issues/new?labels=bug&template=bug_report.md">Report Bug</a>
    &middot;
    <a href="https://github.com/TwiceBoogie/syncturtle-backend/issues/new?labels=enhancement&template=feature_request.md">Request Feature</a>
  </p>
</div>

<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#reason">Reason</a></li>
        <li><a href="#built-with">Built With</a></li>
      </ul>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#installation">Installation</a></li>
      </ul>
    </li>
    <li><a href="#usage">Usage</a></li>
    <li><a href="#roadmap">Roadmap</a></li>
    <li><a href="#contributing">Contributing</a></li>
    <li><a href="#license">License</a></li>
    <li><a href="#contact">Contact</a></li>
    <li><a href="#acknowledgments">Acknowledgments</a></li>
  </ol>
</details>

## About The Project

The Personal Management System is a versatile and user-friendly application designed to help users manage
their personal tasks, events, and more. The app offers less features than that of true psm but it shouldn't
be to hard to add more extensions to it. The backend follows a Domain Driven Design and each "service" follows
the same structure. I've tried to make it organize in case I decide to turn this into a microservice.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

### Reason

In order to further make my understanding of java stronger and learn spring boot along the way, I've decided to make my system
where it wouldn't be too hard to accomplish and features that I could have done on its own. This project allowed me to explore various technologies,
implement best practices, and again valuable experience in "software development" or atleast what I've read and learn in school. I hope this
demonstrates my proficiency in building feature-rich web applications, data management, and user authentication.

### Built With

<!-- Backend core -->

![Java](https://img.shields.io/badge/Java-21-007396?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-6DB33F?logo=springboot&logoColor=white)
![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2024.x-6DB33F?logo=spring&logoColor=white)
![JPA / Hibernate](https://img.shields.io/badge/JPA%20%2F%20Hibernate-6.x-59666C?logo=hibernate&logoColor=white)

<!-- Storage & infra -->

![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-4169E1?logo=postgresql&logoColor=white)
![Liquibase](https://img.shields.io/badge/Liquibase-4.x-2962FF?logo=liquibase&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-7-DC382D?logo=redis&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-28.x-2496ED?logo=docker&logoColor=white)
![HashiCorp Vault](https://img.shields.io/badge/Vault-API%20+%20Spring%20Cloud-000000?logo=vault&logoColor=white)

<!-- Messaging & tracing -->

![RabbitMQ](https://img.shields.io/badge/RabbitMQ-3.x-FF6600?logo=rabbitmq&logoColor=white)
![Apache Kafka](https://img.shields.io/badge/Kafka-3.8-231F20?logo=apachekafka&logoColor=white)
![Micrometer](https://img.shields.io/badge/Micrometer-Tracing-1f77b4?logo=micrometer&logoColor=white)
![Zipkin](https://img.shields.io/badge/Zipkin-Brave-000000?logo=zipkin&logoColor=white)

<!-- Spring ecosystem pieces -->

![Spring Cloud Config](https://img.shields.io/badge/Spring%20Cloud-Config-6DB33F?logo=spring&logoColor=white)
![Eureka](https://img.shields.io/badge/Netflix%20Eureka-Discovery-CC0000?logo=netflix&logoColor=white)
![OpenFeign](https://img.shields.io/badge/OpenFeign-Clients-0F5D92?logo=apache&logoColor=white)

<!-- Testing -->

![JUnit 5](https://img.shields.io/badge/JUnit-5-25A162?logo=junit5&logoColor=white)
![Testcontainers](https://img.shields.io/badge/Testcontainers-1.20-000000?logo=testcontainers&logoColor=white)
![Mockito](https://img.shields.io/badge/Mockito-5-00BFA5?logo=mockito&logoColor=white)

<!-- Build -->

![Maven](https://img.shields.io/badge/Maven-3.x-C71A36?logo=apachemaven&logoColor=white)

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Getting Started

The infrastructure must be run on docker containers, perhaps in the feature I will try to make it work
locally instead of hybrid

### Prerequisites

- Java 21
- Must have Docker engine installed
- Choose a SMTP provider for the email-service
- setup and get creds for AmazonS3

### Installation

1. Clone the repo

```shell
git clone https://github.com/TwiceBoogie/syncturtle-backend.git
```

2. Run infrastructure

```shell
docker compose build vault
docker compose up -d
chmod +x ./infra/scripts/fetch-approle-env.sh
./infra/scripts/fetch-approle-env.sh
```

3. Run spring boot services

```shell
./mvnw clean verify
./mvnw springboot:run
```

The root .env file will be used by the services, so check and make any changes. vscode can help
by using a custom launch.json file which picks up the .env file when running apps using
spring boot dashboard extension

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Usage

## Roadmap

- [x] Add Testing
- [ ] Make Instance-service the main "runner"
- [ ] Multi-language Support
  - [ ] Spanish

See the [open issues](https://github.com/othneildrew/Best-README-Template/issues) for a full list of proposed features (and known issues).

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- CONTRIBUTING -->

## Contributing

Contributions are what make the open source community such an amazing place to learn, inspire, and create. Any contributions you make are **greatly appreciated**.

If you have a suggestion that would make this better, please fork the repo and create a pull request. You can also simply open an issue with the tag "enhancement".
Don't forget to give the project a star! Thanks again!

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## Contact

Salvador Sebastian - salsebastian13@gmail.com
Project Link: [https://github.com/TwiceBoogie/syncturtle-backend](https://github.com/TwiceBoogie/syncturtle-backend)

<p align="right">(<a href="#readme-top">back to top</a>)</p>
