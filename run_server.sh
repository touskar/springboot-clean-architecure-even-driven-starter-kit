#!/bin/bash

echo "🚀 Starting Spring Boot Clean Architecture Server..."
echo "📂 Project: spring-clean-architecture-event-driven-starter"
echo ""

# Compile and run the Spring Boot application
mvn spring-boot:run

echo ""
echo "🎯 Available endpoints:"
echo "  📝 POST /api/v1/register - Create new user"
echo "  📋 GET  /api/v1/users    - List users with pagination"
echo "  🏥 GET  /actuator/health - Health check"
echo ""
echo "📊 Event-driven features:"
echo "  🔔 USER_CREATED event triggers:"
echo "    📧 Notification Handler"
echo "    📈 Analytics Handler"