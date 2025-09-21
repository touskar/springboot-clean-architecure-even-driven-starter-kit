#!/bin/bash

# Generate Event and Handler Script for Spring Boot Clean Architecture
# Usage:
#   ./generate-event.sh --event EventName --entity EntityName --handler HandlerName
#   ./generate-event.sh --event EventName --handler Handler1 --handler Handler2 --handler Handler3
#   ./generate-event.sh --handler-only --event EventName --handler Handler1 --handler Handler2

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Initialize variables
HANDLER_ONLY=false
EVENT_NAME=""
ENTITY_NAME=""
HANDLERS=()

# Function to show help
show_help() {
    echo -e "${BLUE}Event and Handler Generator for Spring Boot Clean Architecture${NC}"
    echo ""
    echo -e "${GREEN}USAGE:${NC}"
    echo "  $0 [--handler-only] --event EventName [--entity EntityName] --handler Handler1 [--handler Handler2 ...]"
    echo ""
    echo -e "${GREEN}OPTIONS:${NC}"
    echo "  --event EventName      Name of the event (without 'Event' suffix)"
    echo "  --entity EntityName    Name of the entity (required for new events)"
    echo "  --handler HandlerName  Name of the handler (without 'Handler' suffix, can be repeated)"
    echo "  --handler-only         Only create handlers for existing event"
    echo "  --help, -h             Show this help message"
    echo ""
    echo -e "${GREEN}EXAMPLES:${NC}"
    echo ""
    echo -e "${BLUE}  # Create event with single handler${NC}"
    echo "  $0 --event UserDeleted --entity User --handler Audit"
    echo ""
    echo -e "${BLUE}  # Create event with multiple handlers${NC}"
    echo "  $0 --event OrderPlaced --entity Order --handler Email --handler Payment --handler Analytics"
    echo ""
    echo -e "${BLUE}  # Add handlers to existing event${NC}"
    echo "  $0 --handler-only --event UserCreated --handler Notification --handler Slack"
    echo ""
    echo -e "${GREEN}GENERATED FILES:${NC}"
    echo "  • Event: src/main/java/{package}/domain/events/{Event}Event.java"
    echo "  • Handler: src/main/java/{package}/application/handlers/{Handler}Handler.java"
    echo ""
    echo -e "${GREEN}AUTO-REGISTRATION:${NC}"
    echo "  All events and handlers are automatically discovered and registered at startup."
}

# Parse command line arguments
while [[ $# -gt 0 ]]; do
    case $1 in
        --handler-only)
            HANDLER_ONLY=true
            shift
            ;;
        --event)
            EVENT_NAME="$2"
            shift 2
            ;;
        --entity)
            ENTITY_NAME="$2"
            shift 2
            ;;
        --handler)
            HANDLERS+=("$2")
            shift 2
            ;;
        --help|-h)
            show_help
            exit 0
            ;;
        *)
            echo -e "${RED}Unknown option: $1${NC}"
            echo ""
            show_help
            exit 1
            ;;
    esac
done

# Validate arguments
if [ -z "$EVENT_NAME" ] || [ ${#HANDLERS[@]} -eq 0 ]; then
    echo -e "${RED}Usage: $0 [--handler-only] --event EventName [--entity EntityName] --handler Handler1 [--handler Handler2 ...]${NC}"
    echo ""
    echo -e "${BLUE}Examples:${NC}"
    echo -e "${BLUE}  # Create event with single handler${NC}"
    echo -e "  $0 --event UserDeleted --entity User --handler Audit"
    echo ""
    echo -e "${BLUE}  # Create event with multiple handlers${NC}"
    echo -e "  $0 --event OrderPlaced --entity Order --handler Email --handler Payment --handler Analytics"
    echo ""
    echo -e "${BLUE}  # Add handlers to existing event${NC}"
    echo -e "  $0 --handler-only --event UserCreated --handler Notification --handler Slack"
    exit 1
fi

# Validate entity is provided for new events
if [ "$HANDLER_ONLY" = false ] && [ -z "$ENTITY_NAME" ]; then
    echo -e "${RED}Error: --entity is required when creating new events${NC}"
    echo -e "${BLUE}Use --handler-only flag to add handlers to existing events${NC}"
    exit 1
fi


# Detect package name dynamically from existing Java files
detect_package() {
    # Try to find the main application class with @SpringBootApplication
    local main_class=$(find src/main/java -name "*.java" -type f -exec grep -l "@SpringBootApplication" {} \; | head -1)

    if [ -n "$main_class" ]; then
        # Extract package from the main class
        BASE_PACKAGE=$(grep "^package " "$main_class" | sed 's/package //; s/;//')
    else
        # Fallback: Find any Java file in domain/entities and extract its package
        local sample_file=$(find src/main/java -path "*/domain/entities/*.java" -type f | head -1)
        if [ -n "$sample_file" ]; then
            local pkg=$(grep "^package " "$sample_file" | sed 's/package //; s/;//')
            # Remove the .domain.entities part to get base package
            BASE_PACKAGE=$(echo "$pkg" | sed 's/\.domain\.entities$//')
        else
            # Last fallback: Find any Java file and derive base package
            local any_file=$(find src/main/java -name "*.java" -type f | head -1)
            if [ -n "$any_file" ]; then
                local pkg=$(grep "^package " "$any_file" | sed 's/package //; s/;//')
                # Try to guess base package (usually first 3 segments)
                BASE_PACKAGE=$(echo "$pkg" | cut -d. -f1-3)
            else
                echo -e "${RED}Error: Could not detect package name. No Java files found.${NC}"
                exit 1
            fi
        fi
    fi
}

# Detect package name
detect_package

# Validate that package was detected
if [ -z "$BASE_PACKAGE" ]; then
    echo -e "${RED}Error: Could not detect base package name${NC}"
    exit 1
fi

# Convert package to path
BASE_PATH="src/main/java/$(echo $BASE_PACKAGE | tr '.' '/')"

# Derived names
EVENT_CLASS="${EVENT_NAME}Event"
if [ "$HANDLER_ONLY" = false ]; then
    ENTITY_LOWER=$(echo "$ENTITY_NAME" | tr '[:upper:]' '[:lower:]')
fi

# File paths
EVENT_PATH="${BASE_PATH}/domain/events/${EVENT_CLASS}.java"

echo -e "${BLUE}===============================================${NC}"
echo -e "${BLUE}Event Generation Script${NC}"
echo -e "${BLUE}===============================================${NC}"
echo ""
echo -e "${GREEN}Detected package:${NC} ${BASE_PACKAGE}"
if [ "$HANDLER_ONLY" = false ]; then
    echo -e "${GREEN}Creating event:${NC} ${EVENT_CLASS}"
    echo -e "${GREEN}Entity type:${NC} ${ENTITY_NAME}"
else
    echo -e "${GREEN}Using existing event:${NC} ${EVENT_CLASS}"
fi
echo -e "${GREEN}Creating handlers:${NC} ${HANDLERS[*]}"
echo ""

# Function to convert snake_case to camelCase
to_camel_case() {
    echo "$1" | sed -r 's/([A-Z])/_\L\1/g' | sed 's/^_//' | tr '[:upper:]' '[:lower:]'
}

# Generate Event Class (if not handler-only mode)
if [ "$HANDLER_ONLY" = false ]; then
    echo -e "${BLUE}Generating Event Class...${NC}"
    cat > "${EVENT_PATH}" << EOF
package ${BASE_PACKAGE}.domain.events;

import ${BASE_PACKAGE}.domain.entities.${ENTITY_NAME};
import ${BASE_PACKAGE}.domain.events.contract.DomainEvent;

/**
 * Event triggered when ${ENTITY_NAME} is ${EVENT_NAME}
 *
 * This event is automatically discovered and registered by the
 * EventRegistrationProcessor at application startup.
 */
public class ${EVENT_CLASS} extends DomainEvent<${ENTITY_NAME}> {

    public ${EVENT_CLASS}(${ENTITY_NAME} ${ENTITY_LOWER}) {
        super(${ENTITY_LOWER});
    }

    @Override
    public String getAggregateId() {
        return data.getId();
    }
}
EOF

    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✓ Created:${NC} ${EVENT_PATH}"
    else
        echo -e "${RED}✗ Failed to create event class${NC}"
        exit 1
    fi
else
    echo -e "${BLUE}Using existing event: ${EVENT_CLASS}${NC}"
fi

# Function to generate a single handler
generate_handler() {
    local handler_name=$1
    local handler_class="${handler_name}Handler"
    local handler_path="${BASE_PATH}/application/handlers/${handler_class}.java"

    echo -e "${BLUE}Generating ${handler_class}...${NC}"
    cat > "${handler_path}" << EOF
package ${BASE_PACKAGE}.application.handlers;

import ${BASE_PACKAGE}.domain.events.${EVENT_CLASS};
import ${BASE_PACKAGE}.infrastructure.messaging.AutoEventHandler;
import ${BASE_PACKAGE}.infrastructure.messaging.EventHandler;
import org.springframework.stereotype.Component;

/**
 * Handler for ${EVENT_CLASS}
 *
 * This handler is automatically discovered and registered to handle
 * ${EVENT_CLASS} events through the @AutoEventHandler annotation.
 */
@Component
@AutoEventHandler(${EVENT_CLASS}.class)
public class ${handler_class} implements EventHandler<${EVENT_CLASS}> {

    @Override
    public void handle(${EVENT_CLASS} event) {
        // TODO: Implement your business logic here
        System.out.println("${handler_class}: Processing ${EVENT_CLASS} for " +
                           event.getData().getClass().getSimpleName() +
                           " with ID: " + event.getAggregateId());

        // Example implementations:
        // - Log to audit trail
        // - Send notifications
        // - Update analytics
        // - Trigger workflows
        // - Update search index
        // - Clear cache
    }
}
EOF

    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✓ Created:${NC} ${handler_path}"
        CREATED_HANDLERS+=("${handler_path}")
    else
        echo -e "${RED}✗ Failed to create ${handler_class}${NC}"
        exit 1
    fi
}

# Generate Handler Classes
CREATED_HANDLERS=()
for handler in "${HANDLERS[@]}"; do
    generate_handler "$handler"
done

echo ""
echo -e "${BLUE}===============================================${NC}"
if [ "$HANDLER_ONLY" = true ]; then
    echo -e "${GREEN}✓ Successfully generated handler!${NC}"
else
    echo -e "${GREEN}✓ Successfully generated event and handler(s)!${NC}"
fi
echo -e "${BLUE}===============================================${NC}"
echo ""
echo -e "${GREEN}Generated Files:${NC}"
file_counter=1
if [ "$HANDLER_ONLY" = false ]; then
    echo "  ${file_counter}. ${EVENT_PATH}"
    ((file_counter++))
fi
for handler_path in "${CREATED_HANDLERS[@]}"; do
    echo "  ${file_counter}. ${handler_path}"
    ((file_counter++))
done
echo ""
echo -e "${GREEN}Next Steps:${NC}"
if [ ${#HANDLERS[@]} -gt 1 ]; then
    echo "  1. Implement the handler logic in each handler's handle() method"
else
    echo "  1. Implement the handler logic in the handler's handle() method"
fi
if [ "$HANDLER_ONLY" = false ]; then
    echo "  2. Publish the event from your use case:"
    echo -e "${BLUE}     ${EVENT_CLASS} event = new ${EVENT_CLASS}(${ENTITY_LOWER});${NC}"
    echo -e "${BLUE}     eventPublisher.publish(event);${NC}"
fi
echo ""
echo -e "${GREEN}The event and handler(s) will be automatically registered at startup!${NC}"