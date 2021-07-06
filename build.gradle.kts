import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.5.2"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
  id ("jacoco")
	kotlin("jvm") version "1.5.20"
	kotlin("plugin.spring") version "1.5.20"
  kotlin("kapt") version "1.5.20"
}

jacoco {
  toolVersion = "0.8.7"
}

group = "br.com.blz"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
	mavenCentral()
}

var mapStructVersion = "1.4.2.Final"

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-web-services")
  implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
  implementation("org.mapstruct:mapstruct:$mapStructVersion")
  implementation("org.apache.commons:commons-lang3:3.12.0")
	testImplementation("org.springframework.boot:spring-boot-starter-test"){
    exclude("org.junit.vintage:junit-vintage-engine")
    exclude("junit:junit")
  }
  testImplementation("io.projectreactor:reactor-test")
  testImplementation("org.hamcrest:hamcrest-library:2.2")
  testImplementation("com.github.javafaker:javafaker:1.0.2")
  testImplementation("org.mockito:mockito-core:3.11.2")
  testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0")

  kapt("org.mapstruct:mapstruct-processor:$mapStructVersion")
  kaptTest("org.mapstruct:mapstruct-processor:$mapStructVersion")


}

configurations {
  implementation{
    exclude(module = "spring-boot-starter-tomcat")
  }
}

tasks.jacocoTestReport{
  reports{
    html.required.set(true)
    xml.required.set(true)
  }
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "11"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

springBoot {
  buildInfo()
}
