#!/bin/bash

echo "🚀 Starting Spring Boot Clean Architecture Starter..."
echo "📂 Module: service-api"
echo ""

# Load environment variables from .env file
if [ -f .env ]; then
    echo "📄 Loading environment variables from .env..."
    set -a
    source .env
    set +a
fi

# Compile and run service-api
mvn spring-boot:run -pl service-api

echo ""
echo "🎯 Available endpoints:"
echo "  📝 POST /api/v1/register - Create new user"
echo "  📋 GET  /api/v1/users    - List users with pagination"
echo "  🏥 GET  /actuator/health - Health check"
echo ""
echo "📊 Event-driven features:"
echo "  🔔 USER_REGISTERED event triggers:"
echo "    📧 Notification Handler"
echo "  🔔 TEST_SHARED event demonstrates:"
echo "    📡 Shared events across modules"