plugins {
	kotlin("jvm") version "2.3.21"
	kotlin("plugin.spring") version "2.3.21"
	id("org.springframework.boot") version "4.1.2"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "br.com.gammacode"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(25)
	}
}

repositories {
	mavenCentral()
}

dependencies {
    // Spring Boot Starters
	implementation("org.springframework.boot:spring-boot-starter-webmvc")
	implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
	implementation("org.springframework.boot:spring-boot-starter-flyway")
	implementation("org.flywaydb:flyway-database-postgresql")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.2.0")

    // Kotlin
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("tools.jackson.module:jackson-module-kotlin")

	runtimeOnly("org.postgresql:postgresql")

    // Test Dependencies
    testImplementation("com.tngtech.archunit:archunit-junit5:1.5.1")
	testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
	testImplementation("org.testcontainers:testcontainers-postgresql")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
	}
}

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
	archiveFileName.set("erp-manager-core.jar")
}

tasks.named("jar") {
	enabled = false
}

tasks.withType<Test> {
	useJUnitPlatform()
	val defaultDockerSocket = file("/var/run/docker.sock")
	val rancherDockerSocket = file("${System.getProperty("user.home")}/.rd/docker.sock")
	if (System.getenv("DOCKER_HOST").isNullOrBlank() && !defaultDockerSocket.exists() && rancherDockerSocket.exists()) {
		environment("DOCKER_HOST", "unix://${rancherDockerSocket.absolutePath}")
		environment("TESTCONTAINERS_DOCKER_SOCKET_OVERRIDE", "/var/run/docker.sock")
	}
}
