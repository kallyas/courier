# Courier Service

A Spring Boot application for managing courier services.

## CI/CD and Test Coverage

This project uses GitHub Actions for continuous integration and test coverage reporting.

### GitHub Workflow

The GitHub workflow is configured to:

1. Build the project
2. Run tests
3. Generate test coverage reports
4. Upload coverage reports as artifacts
5. Add coverage information to pull requests

The workflow runs on:
- Every push to the `main` branch
- Every pull request to the `main` branch

### Test Coverage

Test coverage is measured using JaCoCo. The minimum required coverage is set to 50%.

To run tests and generate coverage reports locally:

```bash
./gradlew test jacocoTestReport
```

The coverage reports will be available at:
- HTML: `build/reports/jacoco/index.html`
- XML: `build/reports/jacoco/test/jacocoTestReport.xml`

## Development

### Prerequisites

- JDK 21
- Gradle
- PostgreSQL

### Building the Project

```bash
./gradlew build
```

### Running the Application

```bash
./gradlew bootRun
```