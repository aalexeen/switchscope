# SwitchScope

> Comprehensive Network Infrastructure Management System

[![License: BSL 1.1](https://img.shields.io/badge/License-BSL%201.1-blue.svg)](LICENSE)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green.svg)](https://spring.io/projects/spring-boot)
[![Vue](https://img.shields.io/badge/Vue-3.x-4FC08D.svg)](https://vuejs.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-13+-blue.svg)](https://postgresql.org/)

## Overview

SwitchScope is a modern web-based platform designed to provide complete visibility and control over network infrastructure. The system offers comprehensive management of network switches, ports, patch panels, and equipment inventory with real-time monitoring capabilities.

**Perfect for:** Healthcare organizations, educational institutions, enterprise networks, and government facilities seeking professional network infrastructure management without enterprise licensing costs.

## Key Features

### 🔍 **Infrastructure Discovery & Inventory**
- Automated network device discovery via SNMP
- Complete equipment inventory management
- Physical location tracking with IT closets
- Cable connection mapping and documentation
- Support for copper, fiber, and mixed infrastructures

### 📊 **Real-time Monitoring**  
- Switch and port status monitoring
- MAC address tracking and history
- DHCP server integration and synchronization
- Historical performance analytics
- Broadcast storm detection and prevention

### ⚙️ **Configuration Management**
- Web-based switch configuration interface
- SSH and Telnet device communication
- Role-based access control with multiple permission levels
- Change tracking, auditing, and rollback capabilities
- Configuration backup and automated restore

### 🎫 **Workflow & Ticketing System**
- Location-based ticket system
- Equipment-specific issue tracking  
- Multi-level approval workflows
- User delegation and task management
- Integration with existing ITSM systems

### 🏥 **Healthcare & Enterprise Ready**
- HIPAA compliance considerations
- Multi-tenant architecture support
- Active Directory/LDAP integration
- Audit trails for regulatory compliance
- 24/7 monitoring capabilities

## Technology Stack

### Backend
- **Java 21** with **Spring Boot 3.x** framework
- **Spring Security** for authentication and authorization
- **Spring Data JPA** for database operations
- **PostgreSQL 13+** database with JSONB support
- **Liquibase** for database version control
- **Redis** for caching and session management
- **SSHJ** for secure device communication
- **ExpectIt** for CLI automation and parsing
- **SNMP4J** for SNMP protocol support

### Frontend  
- **Vue 3** with Composition API
- **Quasar Framework** for professional UI components
- **Pinia** for state management
- **D3.js** and **Vis.js** for network topology visualization
- **Chart.js** for performance analytics
- **Axios** for HTTP client with interceptors
- Responsive design optimized for desktop and mobile

### DevOps & Infrastructure
- **Docker** and **Docker Compose** for containerization
- **GitHub Actions** for CI/CD pipeline
- **SonarQube** integration for code quality
- **Prometheus** and **Grafana** for application monitoring
- **nginx** reverse proxy configuration
- **Let's Encrypt** SSL automation

## Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Vue 3         │    │  Spring Boot    │    │   PostgreSQL    │
│   Frontend      │◄──►│  Backend        │◄──►│   + TimescaleDB │
│   + Quasar      │    │  + Security     │    │   Extension     │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                              │                         │
                    ┌─────────────────┐       ┌─────────────────┐
                    │     Redis       │       │   Liquibase     │
                    │ (Cache + Jobs)  │       │   Migrations    │
                    └─────────────────┘       └─────────────────┘
                              │
                    ┌─────────────────┐
                    │  Network Layer  │
                    │  SNMP + SSH     │
                    │  + ExpectIt     │
                    └─────────────────┘
                              │
                    ┌─────────────────┐
                    │ Network Devices │
                    │ Switches, APs,  │
                    │ Patch Panels    │
                    └─────────────────┘
```

## Core Data Model

The system is built around 7 main entities:

```
Location (IT Closets, Floors, Buildings)
    ├── Equipment (Active & Passive Network Equipment)
        ├── NetworkSwitch (Managed L2/L3 Switches)
        ├── PatchPanel (Passive Connection Panels)
        └── DataPort (Physical RJ45/Fiber Ports)
            └── Cable (Copper/Fiber Connections)
                └── Connection (Logical Port-to-Port Links)
```

**Key Relationships:**
- **Locations** contain multiple pieces of equipment
- **Equipment** has multiple data ports
- **Cables** create physical connections between ports
- **Connections** represent logical relationships and data flows

## Installation & Quick Start

### Prerequisites
- **Java 21** or higher (OpenJDK recommended)
- **Node.js 18+** and **npm 9+**
- **PostgreSQL 13+** with TimescaleDB extension (optional)
- **Redis 6+** for optimal performance (optional for development)
- **Docker** and **Docker Compose** (recommended)

### Option 1: Docker Compose (Recommended)

1. **Clone and start:**
   ```bash
   git clone https://github.com/aalexeen/switchscope.git
   cd switchscope
   
   # Start all services
   docker-compose up -d
   
   # View logs
   docker-compose logs -f
   ```

2. **Access the application:**
   - Frontend: http://localhost:3000
   - Backend API: http://localhost:8080
   - API Documentation: http://localhost:8080/swagger-ui.html

### Option 2: Manual Installation

1. **Database Setup:**
   ```bash
   # Install PostgreSQL and create database
   sudo -u postgres createdb switchscope
   sudo -u postgres createuser switchscope_user --password
   
   # Optional: Install TimescaleDB for time-series data
   sudo apt install timescaledb-postgresql
   ```

2. **Backend Setup:**
   ```bash
   cd backend
   
   # Configure database connection
   cp src/main/resources/application.properties.example \
      src/main/resources/application.properties
   
   # Edit database credentials and SNMP settings
   nano src/main/resources/application.properties
   
   # Run database migrations
   ./mvnw liquibase:update
   
   # Start backend server
   ./mvnw spring-boot:run
   ```

3. **Frontend Setup:**
   ```bash
   cd frontend
   
   # Install dependencies
   npm install
   
   # Start development server
   npm run dev
   
   # Or build for production
   npm run build
   ```

### Configuration

Copy and customize configuration files:

```bash
# Backend configuration
cp backend/src/main/resources/application.properties.example \
   backend/src/main/resources/application.properties

# Frontend configuration  
cp frontend/.env.example frontend/.env.local
```

**Key configuration settings:**
- Database connection (PostgreSQL)
- Redis cache settings (optional)
- SNMP community strings and credentials
- SSH connection timeouts and retry policies
- LDAP/Active Directory integration (if needed)

## Development Roadmap

### ✅ Phase 1: Foundation (Q1 2025)
- [x] Database schema and core entities
- [x] Basic CRUD operations for all entities
- [x] Connection tracking and relationship management
- [x] Basic web interface with Vue 3 + Quasar
- [x] Docker containerization

### 🔄 Phase 2: Monitoring (Q2 2025)
- [ ] SNMP integration for device discovery
- [ ] Real-time switch port status monitoring
- [ ] MAC address tracking and history
- [ ] Performance metrics collection
- [ ] Alerting system with email/Slack notifications

### 🎯 Phase 3: Configuration (Q3 2025)  
- [ ] SSH-based device configuration
- [ ] Web-based switch port management
- [ ] User access controls and role management
- [ ] Configuration templates and automation
- [ ] Change approval workflows

### 🚀 Phase 4: Enterprise Features (Q4 2025)
- [ ] Advanced ticketing system
- [ ] DHCP server synchronization
- [ ] LDAP/Active Directory integration
- [ ] Advanced analytics and reporting
- [ ] REST API for third-party integrations
- [ ] Mobile application (iOS/Android)

### 🎊 Phase 5: Advanced Analytics (2026)
- [ ] Machine learning for anomaly detection
- [ ] Predictive maintenance capabilities
- [ ] Advanced network topology mapping
- [ ] Integration with network monitoring tools (Nagios, Zabbix)
- [ ] Compliance reporting (SOX, HIPAA, PCI-DSS)

## API Documentation

### REST API Endpoints

Once running, comprehensive API documentation is available at:
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI 3.0 Spec**: `http://localhost:8080/v3/api-docs`
- **Redoc**: `http://localhost:8080/redoc`

### Key API Endpoints:
```
GET    /api/v1/switches                 # List all switches
POST   /api/v1/switches                 # Create new switch
GET    /api/v1/switches/{id}/ports      # Get switch ports
PUT    /api/v1/ports/{id}/config        # Update port configuration
GET    /api/v1/locations                # List all locations
POST   /api/v1/connections              # Create new connection
GET    /api/v1/cables                   # List all cables
GET    /api/v1/monitoring/status        # Get real-time status
```

## Security & Compliance

### Authentication & Authorization
- **JWT-based authentication** with refresh tokens
- **Role-based access control (RBAC)** with granular permissions
- **Multi-factor authentication (MFA)** support via TOTP
- **Session management** with Redis-based storage
- **Password policies** with complexity requirements

### Data Protection
- **Encryption at rest** for sensitive configuration data
- **TLS/SSL encryption** for all API communications  
- **Audit logging** for all configuration changes
- **Backup and recovery** procedures for critical data
- **GDPR compliance** considerations for user data

### Network Security  
- **SSH key-based authentication** for device access
- **SNMPv3** support with encryption and authentication
- **Network segmentation** recommendations
- **Firewall rules** documentation for required ports
- **Vulnerability scanning** integration with security tools

## Contributing

We welcome contributions from the community! Please see [CONTRIBUTING.md](CONTRIBUTING.md) for detailed guidelines.

### Development Setup
1. Fork the repository
2. Create a feature branch: `git checkout -b feature/amazing-feature`
3. Set up pre-commit hooks: `npm run prepare`
4. Make your changes and add tests
5. Run the test suite: `npm run test:all`
6. Submit a pull request with a clear description

### Code Standards
- **Backend**: Java with Google Style Guide
- **Frontend**: Vue 3 Composition API with TypeScript
- **Database**: PostgreSQL best practices with proper indexing
- **Testing**: Minimum 80% code coverage required
- **Documentation**: All public APIs must be documented

## Testing

### Backend Tests
```bash
cd backend
./mvnw test                    # Unit tests
./mvnw integration-test       # Integration tests  
./mvnw verify                 # Full test suite
```

### Frontend Tests  
```bash
cd frontend
npm run test:unit             # Unit tests with Vitest
npm run test:e2e              # End-to-end tests with Cypress
npm run test:coverage         # Coverage report
```

### Load Testing
```bash
# Using k6 for performance testing
k6 run tests/performance/load-test.js
```

## Deployment

### Production Deployment

1. **Using Docker Compose:**
   ```bash
   # Production environment
   docker-compose -f docker-compose.prod.yml up -d
   
   # With SSL and nginx reverse proxy
   docker-compose -f docker-compose.prod.yml -f docker-compose.ssl.yml up -d
   ```

2. **Kubernetes Deployment:**
   ```bash
   # Apply Kubernetes manifests
   kubectl apply -f k8s/
   
   # Or using Helm chart
   helm install switchscope ./helm/switchscope
   ```

3. **Environment Variables:**
   ```bash
   # Required production settings
   SPRING_PROFILES_ACTIVE=production
   DATABASE_URL=postgresql://switchscope:password@db:5432/switchscope
   REDIS_URL=redis://redis:6379
   JWT_SECRET=your-256-bit-secret-key
   
   # Java 21 Virtual Threads settings
   SPRING_THREADS_VIRTUAL_ENABLED=true
   JAVA_OPTS="-XX:+UseG1GC -XX:+UseContainerSupport -Xms512m -Xmx4g --enable-preview"
   ```

### Monitoring & Logging

- **Application Metrics**: Prometheus + Grafana dashboards
- **Log Aggregation**: ELK Stack (Elasticsearch, Logstash, Kibana)  
- **Health Checks**: Spring Boot Actuator endpoints
- **Error Tracking**: Sentry integration for production errors
- **Performance Monitoring**: APM with New Relic or DataDog

## License

This project is licensed under the **Business Source License 1.1** - see the [LICENSE](LICENSE) file for complete details.

### License Summary:
- ✅ **Free for healthcare, educational, and government use**
- ✅ **Free for internal business operations**
- ✅ **Open source development and contributions welcome**
- ❌ **Cannot be offered as a competing commercial service**
- 🕐 **Becomes Apache 2.0 licensed on January 1, 2029**

### Additional Use Grant:
```
You may use SwitchScope for:
• Internal operations of healthcare organizations  
• Educational institutions and research
• Government and municipal organizations
• Non-profit organizations
• Personal and development use
```

**For commercial licensing or enterprise support, contact:** [your-email@example.com]

## Support & Community

### Getting Help
- 📖 **Documentation**: [docs.switchscope.io](https://docs.switchscope.io)
- 🐛 **Issue Tracker**: [GitHub Issues](https://github.com/aalexeen/switchscope/issues)
- 💬 **Discussions**: [GitHub Discussions](https://github.com/aalexeen/switchscope/discussions)
- 📧 **Email Support**: [support@switchscope.io](mailto:support@switchscope.io)
- 💬 **Discord**: [Community Discord Server](https://discord.gg/switchscope)

### Commercial Support
- **Professional Services**: Implementation, customization, training
- **Enterprise Support**: 24/7 support with SLA guarantees
- **Custom Development**: Feature development and integration services
- **Compliance Consulting**: HIPAA, SOX, PCI-DSS compliance assistance

### Roadmap & Feature Requests
- View our [public roadmap](https://github.com/aalexeen/switchscope/projects/1)
- Submit [feature requests](https://github.com/aalexeen/switchscope/discussions/categories/ideas)
- Vote on [upcoming features](https://github.com/aalexeen/switchscope/discussions/categories/polls)

## Acknowledgments

### Technology Stack
Built with industry-leading open source technologies:
- **Spring Framework** team for excellent enterprise Java framework
- **Vue.js** team for the progressive JavaScript framework
- **PostgreSQL** community for the robust database system
- **Quasar Framework** for the comprehensive Vue UI library

### Inspiration
- **NetBox** for network infrastructure modeling concepts
- **LibreNMS** for network monitoring approaches  
- **Nautobot** for modern network automation patterns
- **OpenNMS** for enterprise network management practices

### Contributors
Thanks to all contributors who help make SwitchScope better! 🙏

---

<div align="center">

**SwitchScope** - *Bringing clarity to your network infrastructure*

Made with ❤️ for network administrators worldwide

[Website](https://switchscope.io) • [Documentation](https://docs.switchscope.io) • [Demo](https://demo.switchscope.io)

</div>