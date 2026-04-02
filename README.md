# Payroll Calculator

## 1. Project Overview

A production-ready employee payroll calculator system that computes monthly salary for employees based on their type and applicable deductions. The system supports three employee types (FULL_TIME, PART_TIME, CONTRACTOR) with progressive tax brackets and multiple deduction types.

## 2. Features

- **Employee Type Support**: FULL_TIME (fixed monthly salary), PART_TIME (hourly rate × hours, max 120 hours/month), CONTRACTOR (daily rate × days)
- **Progressive Tax Calculation**: 0% for first $1,000, 10% for $1,001-$3,000, 20% for $3,001-$5,000, 30% above $5,000
- **Deduction Management**: Health insurance ($150 for FULL_TIME), retirement contribution (5% optional), union dues ($50 optional)
- **Comprehensive Validation**: Input validation with custom exceptions for invalid data
- **High Test Coverage**: 153 unit tests with >80% code coverage
- **Production-Ready**: Follows SOLID principles, design patterns, and OWASP security best practices

## 3. Tech Stack

- **Language**: Java 11
- **Build Tool**: Maven 3.6+
- **Testing**: JUnit 5, Mockito, AssertJ
- **Code Coverage**: JaCoCo
- **Logging**: SLF4J + Logback
- **Validation**: Bean Validation API

## 4. Project Structure

```
src/
├── main/
│   ├── java/com/intuit/payroll/
│   │   ├── domain/          # Employee, PaySlip, EmployeeType, EmployeeBuilder
│   │   ├── strategy/        # Pay calculation strategies (Strategy Pattern)
│   │   ├── calculator/      # TaxCalculator, DeductionCalculator
│   │   ├── processor/       # PayrollProcessor (main orchestrator)
│   │   ├── exception/       # Custom exceptions
│   │   ├── chain/           # Tax bracket handlers (Chain of Responsibility)
│   │   ├── util/            # ValidationUtils, PayrollConstants
│   │   └── app/             # PayrollDemo (demo application)
│   └── resources/
│       ├── logback.xml      # Logging configuration
│       └── application.properties
└── test/
    └── java/                # Comprehensive unit tests (153 tests)
```

## 5. Design Decisions

### Design Patterns
- **Strategy Pattern**: Different pay calculation strategies per employee type for extensibility
- **Builder Pattern**: Employee construction with validation for type safety
- **Chain of Responsibility**: Progressive tax bracket calculation for maintainability
- **Template Method**: Payroll processing flow (gross → tax → deductions → net)

### SOLID Principles
- **Single Responsibility**: Each class has one reason to change
- **Open/Closed**: Open for extension (new employee types), closed for modification
- **Liskov Substitution**: Strategy implementations are interchangeable
- **Interface Segregation**: Small, focused interfaces
- **Dependency Inversion**: Depend on abstractions (interfaces)

### Security (OWASP)
- Input validation at all entry points
- Error messages don't expose internal details
- Immutable domain objects
- BigDecimal for currency precision
- Proper exception handling and logging

## 6. Edge Cases Handled

- **Null Validation**: All inputs validated for null values
- **Invalid Inputs**: Negative/zero pay rates, invalid hours/days
- **Boundary Conditions**: Tax bracket boundaries ($1,000, $3,000, $5,000)
- **Zero Gross Pay**: Handles employees with zero hours/days worked
- **Negative Net Pay**: Sets to zero when deductions exceed gross pay minus tax
- **Invalid Employee Types**: Missing strategy handling
- **Fractional Days**: Contractors must work whole days
- **Hours Limits**: PART_TIME employees limited to 0-120 hours/month
- **Rounding**: All currency values rounded to 2 decimal places (HALF_UP)

## 7. How to Run the Project

### Prerequisites
- Java 11 or higher
- Maven 3.6 or higher

### Build and Compile
```bash
mvn clean compile
```

### Run Demo Application
```bash
mvn exec:java
```

The demo creates 6 employees covering all types and deduction combinations, processes payroll, and displays results.

## 8. Running Tests

### Run All Tests
```bash
mvn test
```

### Generate Coverage Report
```bash
mvn jacoco:report
# Open target/site/jacoco/index.html in browser
```

### Test Results
- **Total Tests**: 153
- **Failures**: 0
- **Errors**: 0
- **Code Coverage**: 81% overall (100% for core business logic)

## 9. Future Improvements

1. **Database Persistence**: Add JPA entities and repository layer for persistent storage of employees and pay history
2. **REST API**: Add Spring Boot REST endpoints with Swagger/OpenAPI documentation for integration capabilities

---

## License

This project is part of a take-home assignment for Java Core.
