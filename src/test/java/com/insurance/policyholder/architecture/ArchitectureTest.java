package com.insurance.policyholder.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

/**
 * Architecture tests to enforce hexagonal architecture constraints.
 * Validates dependency direction: Infrastructure → Application → Domain
 */
@DisplayName("Hexagonal Architecture Tests")
class ArchitectureTest {

    private static final String BASE_PACKAGE = "com.insurance.policyholder";
    private static final String DOMAIN_PACKAGE = BASE_PACKAGE + ".domain..";
    private static final String APPLICATION_PACKAGE = BASE_PACKAGE + ".application..";
    private static final String INFRASTRUCTURE_PACKAGE = BASE_PACKAGE + ".infrastructure..";

    private static JavaClasses importedClasses;

    @BeforeAll
    static void setup() {
        importedClasses = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages(BASE_PACKAGE);
    }

    @Nested
    @DisplayName("Layered Architecture")
    class LayeredArchitectureTests {

        @Test
        @DisplayName("應遵循六角形架構分層：Infrastructure → Application → Domain")
        void shouldFollowHexagonalArchitectureLayers() {
            layeredArchitecture()
                    .consideringOnlyDependenciesInLayers()
                    .layer("Domain").definedBy(DOMAIN_PACKAGE)
                    .layer("Application").definedBy(APPLICATION_PACKAGE)
                    .layer("Infrastructure").definedBy(INFRASTRUCTURE_PACKAGE)
                    .whereLayer("Domain").mayNotAccessAnyLayer()
                    .whereLayer("Application").mayOnlyAccessLayers("Domain")
                    .whereLayer("Infrastructure").mayOnlyAccessLayers("Application", "Domain")
                    .check(importedClasses);
        }
    }

    @Nested
    @DisplayName("Domain Layer Constraints")
    class DomainLayerTests {

        @Test
        @DisplayName("Domain Layer 不應依賴 Application Layer")
        void domainShouldNotDependOnApplication() {
            noClasses()
                    .that().resideInAPackage(DOMAIN_PACKAGE)
                    .should().dependOnClassesThat().resideInAPackage(APPLICATION_PACKAGE)
                    .because("Domain Layer must not depend on Application Layer (Hexagonal Architecture)")
                    .check(importedClasses);
        }

        @Test
        @DisplayName("Domain Layer 不應依賴 Infrastructure Layer")
        void domainShouldNotDependOnInfrastructure() {
            noClasses()
                    .that().resideInAPackage(DOMAIN_PACKAGE)
                    .should().dependOnClassesThat().resideInAPackage(INFRASTRUCTURE_PACKAGE)
                    .because("Domain Layer must not depend on Infrastructure Layer (Hexagonal Architecture)")
                    .check(importedClasses);
        }

        @Test
        @DisplayName("Domain Layer 不應使用 Spring 註解")
        void domainShouldNotUseSpringAnnotations() {
            noClasses()
                    .that().resideInAPackage(DOMAIN_PACKAGE)
                    .should().dependOnClassesThat().resideInAPackage("org.springframework..")
                    .because("Domain Layer must be framework-independent (Pure Java)")
                    .check(importedClasses);
        }

        @Test
        @DisplayName("Domain Layer 不應使用 JPA/Hibernate 註解")
        void domainShouldNotUseJpaAnnotations() {
            noClasses()
                    .that().resideInAPackage(DOMAIN_PACKAGE)
                    .should().dependOnClassesThat().resideInAnyPackage(
                            "jakarta.persistence..",
                            "javax.persistence..",
                            "org.hibernate.."
                    )
                    .because("Domain Layer must not depend on JPA/Hibernate (Pure Java)")
                    .check(importedClasses);
        }
    }

    @Nested
    @DisplayName("Application Layer Constraints")
    class ApplicationLayerTests {

        @Test
        @DisplayName("Application Layer 不應依賴 Infrastructure Layer")
        void applicationShouldNotDependOnInfrastructure() {
            noClasses()
                    .that().resideInAPackage(APPLICATION_PACKAGE)
                    .should().dependOnClassesThat().resideInAPackage(INFRASTRUCTURE_PACKAGE)
                    .because("Application Layer must not depend on Infrastructure Layer (Hexagonal Architecture)")
                    .check(importedClasses);
        }

        @Test
        @DisplayName("Application Layer 可以依賴 Domain Layer")
        void applicationCanDependOnDomain() {
            classes()
                    .that().resideInAPackage(APPLICATION_PACKAGE)
                    .should().onlyDependOnClassesThat()
                    .resideInAnyPackage(
                            APPLICATION_PACKAGE,
                            DOMAIN_PACKAGE,
                            "java..",
                            "jakarta..",
                            "org.springframework.stereotype..",
                            "org.springframework.transaction..",
                            "org.springframework.beans..",
                            "org.springframework.data.domain.." // For Page, Pageable
                    )
                    .because("Application Layer should only depend on Domain Layer and basic Spring annotations")
                    .check(importedClasses);
        }
    }

    @Nested
    @DisplayName("Infrastructure Layer Constraints")
    class InfrastructureLayerTests {

        @Test
        @DisplayName("Infrastructure Layer 可以依賴 Application 和 Domain Layers")
        void infrastructureCanDependOnApplicationAndDomain() {
            classes()
                    .that().resideInAPackage(INFRASTRUCTURE_PACKAGE)
                    .should().onlyDependOnClassesThat()
                    .resideInAnyPackage(
                            INFRASTRUCTURE_PACKAGE,
                            APPLICATION_PACKAGE,
                            DOMAIN_PACKAGE,
                            "java..",
                            "jakarta..",
                            "org.springframework..",
                            "org.springdoc..",
                            "io.swagger..",
                            "com.fasterxml..",
                            "org.slf4j.."
                    )
                    .check(importedClasses);
        }
    }

    @Nested
    @DisplayName("Port and Adapter Pattern")
    class PortAndAdapterTests {

        @Test
        @DisplayName("Port 介面應定義在 Application Layer")
        void portsShouldBeInApplicationLayer() {
            classes()
                    .that().resideInAPackage(BASE_PACKAGE + ".application.port..")
                    .should().beInterfaces()
                    .because("Ports should be interfaces defined in Application Layer")
                    .check(importedClasses);
        }
    }
}
