plugins {
	id("java")
	id("org.springframework.boot") version "3.2.1"
	id("io.spring.dependency-management") version "1.1.4"
	id("org.openjfx.javafxplugin") version "0.1.0"
}

group = "com.neighbornotebook"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_21
}

repositories {
	mavenCentral()
	maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/") }
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("com.fasterxml.jackson.core:jackson-databind")
	implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
	implementation("org.apache.commons:commons-lang3")
	implementation("commons-io:commons-io:2.15.1")
	implementation("org.slf4j:slf4j-api")
	implementation("ch.qos.logback:logback-classic")
	
	// RichTextFX для подсветки синтаксиса
	implementation("org.fxmisc.richtext:richtextfx:0.11.2")
	implementation("org.fxmisc.flowless:flowless:0.7.2")
	
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")
	
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.junit.jupiter:junit-jupiter")
	testImplementation("org.mockito:mockito-junit-jupiter")
	testImplementation("org.testfx:testfx-core:4.0.17")
	testImplementation("org.testfx:testfx-junit5:4.0.17")
}

javafx {
	version = "21"
	modules = listOf("javafx.controls", "javafx.fxml")
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
