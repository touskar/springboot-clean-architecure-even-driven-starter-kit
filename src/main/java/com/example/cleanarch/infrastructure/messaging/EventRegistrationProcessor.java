package com.example.cleanarch.infrastructure.messaging;

import com.example.cleanarch.domain.events.contract.DomainEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.Set;
import java.util.logging.Logger;

@Component
public class EventRegistrationProcessor {

    private static final Logger logger = Logger.getLogger(EventRegistrationProcessor.class.getName());

    @Autowired
    private EventRegistry eventRegistry;

    @Autowired
    private ApplicationContext applicationContext;

    @PostConstruct
    public void scanAndRegisterEvents() {
        // Dynamically detect base package from @SpringBootApplication
        String basePackage = getBasePackage();

        // Register events
        scanAndRegisterEventTypes(basePackage);

        // Register event handlers
        scanAndRegisterEventHandlers(basePackage);
    }

    private void scanAndRegisterEventTypes(String basePackage) {
        String eventsPackage = basePackage + ".domain.events";
        logger.info("Scanning for events in package: " + eventsPackage);

        ClassPathScanningCandidateComponentProvider scanner =
            new ClassPathScanningCandidateComponentProvider(false);

        // Look for all classes that extend DomainEvent (no annotation needed)
        scanner.addIncludeFilter((metadataReader, metadataReaderFactory) -> {
            try {
                Class<?> clazz = Class.forName(metadataReader.getClassMetadata().getClassName());
                return DomainEvent.class.isAssignableFrom(clazz)
                       && !clazz.equals(DomainEvent.class);
            } catch (ClassNotFoundException e) {
                return false;
            }
        });

        Set<BeanDefinition> candidateComponents =
            scanner.findCandidateComponents(eventsPackage);

        for (BeanDefinition beanDefinition : candidateComponents) {
            try {
                Class<?> eventClass = Class.forName(beanDefinition.getBeanClassName());
                eventRegistry.registerEventType(eventClass);
                logger.info("Auto-registered event: " + eventClass.getSimpleName());
            } catch (ClassNotFoundException e) {
                logger.warning("Could not load event class: " + beanDefinition.getBeanClassName());
            }
        }
    }

    private void scanAndRegisterEventHandlers(String basePackage) {
        String handlersPackage = basePackage + ".application.handlers";
        logger.info("Scanning for event handlers in package: " + handlersPackage);

        ClassPathScanningCandidateComponentProvider scanner =
            new ClassPathScanningCandidateComponentProvider(false);

        // Look for classes annotated with @AutoEventHandler
        scanner.addIncludeFilter(new AnnotationTypeFilter(AutoEventHandler.class));

        Set<BeanDefinition> candidateComponents =
            scanner.findCandidateComponents(handlersPackage);

        for (BeanDefinition beanDefinition : candidateComponents) {
            try {
                Class<?> handlerClass = Class.forName(beanDefinition.getBeanClassName());
                AutoEventHandler annotation = handlerClass.getAnnotation(AutoEventHandler.class);

                if (annotation != null) {
                    Class<? extends DomainEvent> eventClass = annotation.value();
                    Object handlerBean = applicationContext.getBean(handlerClass);

                    if (handlerBean instanceof EventHandler) {
                        // Use raw types to avoid generics issues during registration
                        eventRegistry.registerHandler((Class) eventClass, (EventHandler) handlerBean);
                        logger.info("Auto-registered handler: " + handlerClass.getSimpleName() + " for event: " + eventClass.getSimpleName());
                    }
                }
            } catch (ClassNotFoundException e) {
                logger.warning("Could not load handler class: " + beanDefinition.getBeanClassName());
            }
        }
    }

    private String getBasePackage() {
        // Find the class annotated with @SpringBootApplication
        String[] beanNames = applicationContext.getBeanNamesForAnnotation(SpringBootApplication.class);

        if (beanNames.length > 0) {
            try {
                Class<?> mainClass = applicationContext.getBean(beanNames[0]).getClass();
                String packageName = mainClass.getPackage().getName();
                logger.info("Detected base package from @SpringBootApplication: " + packageName);
                return packageName;
            } catch (Exception e) {
                logger.warning("Could not detect main class package: " + e.getMessage());
            }
        }

        // Fallback: use current package and go up to find base
        String currentPackage = this.getClass().getPackage().getName();
        String basePackage = currentPackage.substring(0, currentPackage.lastIndexOf('.', currentPackage.lastIndexOf('.') - 1));
        logger.info("Using fallback base package: " + basePackage);
        return basePackage;
    }
}