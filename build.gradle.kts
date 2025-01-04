plugins {
	id("java")
	id("org.springframework.boot") version "3.2.1"
	id("io.spring.dependency-management") version "1.1.4"
	id("org.openjfx.javafxplugin") version "0.0.13"
}

group = "com.neighbornotebook"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion.set(JavaLanguageVersion.of(21))
	}
}

repositories {
	mavenCentral()
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
	all {
		exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
	}
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("ch.qos.logback:logback-classic")
	implementation(platform("org.openjfx:javafx-bom:21.0.1"))
	implementation("org.openjfx:javafx-controls")
	implementation("org.openjfx:javafx-fxml")
	implementation("org.openjfx:javafx-graphics")
	implementation("org.openjfx:javafx-base")
	implementation("com.fasterxml.jackson.core:jackson-databind")
	implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
	
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")
	
	testImplementation("org.springframework.boot:spring-boot-starter-test")
}

javafx {
	version = "21.0.1"
	modules = listOf("javafx.controls", "javafx.fxml", "javafx.graphics", "javafx.base")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.withType<JavaExec> {
	systemProperty("file.encoding", "UTF-8")
	jvmArgs = listOf(
		"-Dfile.encoding=UTF-8",
		"--add-opens", "java.base/java.lang=ALL-UNNAMED",
		"--add-opens", "java.base/java.nio=ALL-UNNAMED",
		"--add-opens", "java.base/java.util=ALL-UNNAMED",
		"--add-exports", "javafx.graphics/com.sun.javafx.application=ALL-UNNAMED",
		"--module-path", classpath.asPath,
		"--add-modules", "javafx.controls,javafx.fxml,javafx.graphics,javafx.base"
	)
}
