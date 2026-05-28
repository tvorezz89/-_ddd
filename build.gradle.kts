import com.google.protobuf.gradle.id
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.net.URI

plugins {
    id("org.springframework.boot") version "3.5.5"
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("jvm") version "2.1.0"
    kotlin("plugin.spring") version "2.1.0"
    kotlin("plugin.jpa") version "2.1.0"
    id("com.google.protobuf") version "0.9.4"
    id("org.openapi.generator") version "7.22.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("org.jlleitschuh.gradle.ktlint") version "13.1.0"
}

group = "microarch"
version = "1.0.0"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

kotlin {
    sourceSets {
        main {
            kotlin.srcDir("src/main/kotlin")
            kotlin.srcDir(layout.buildDirectory.dir("generated/openapi/src/main/kotlin").get().asFile)
        }
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.kafka:spring-kafka")

    implementation("org.postgresql:postgresql")

    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.19.2")

    implementation("io.swagger.core.v3:swagger-annotations:2.2.14")
    implementation("org.openapitools:jackson-databind-nullable:0.2.6")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.5")
    implementation("jakarta.validation:jakarta.validation-api:3.0.2")

    implementation("ch.qos.logback:logback-classic:1.5.13")
    implementation("org.slf4j:slf4j-api:2.0.13")

    implementation("io.grpc:grpc-netty-shaded:1.57.2")
    implementation("io.grpc:grpc-protobuf:1.57.2")
    implementation("io.grpc:grpc-stub:1.57.2")
    implementation("com.google.protobuf:protobuf-kotlin:4.28.2")
    implementation("com.google.protobuf:protobuf-java-util:3.24.3")
    implementation("com.google.guava:guava:32.1.2-jre")

    compileOnly("jakarta.annotation:jakarta.annotation-api:2.1.1")
    compileOnly("javax.annotation:javax.annotation-api:1.3.2")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
    testImplementation("org.assertj:assertj-core:3.24.2")
    testImplementation("org.testcontainers:postgresql:1.21.3")
    testImplementation("org.testcontainers:junit-jupiter:1.21.3")
    testImplementation("org.reflections:reflections:0.10.2")
}

protobuf {
    val protocVersion = "3.24.3"
    val grpcVersion = "1.57.2"

    protoc {
        artifact = "com.google.protobuf:protoc:$protocVersion"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:$grpcVersion"
        }
    }
    generateProtoTasks {
        ofSourceSet("main").forEach { task ->
            task.builtins {
                id("kotlin")
            }
            task.plugins {
                id("grpc")
            }
        }
    }
}

val openapiSpecFile = file("openapi.yml")
if (!openapiSpecFile.exists()) {
    val url = URI(
        "https://gitlab.com/microarch-ru/ddd-in-practice/system-design/-/raw/main/services/delivery/contracts/openapi.yml?ref_type=heads"
    ).toURL()
    url.openStream().use { input ->
        openapiSpecFile.outputStream().use { output ->
            input.copyTo(output)
        }
    }
}

openApiGenerate {
    inputSpec.set(openapiSpecFile.absolutePath)
    generatorName.set("kotlin-spring")
    outputDir.set(layout.buildDirectory.dir("generated/openapi").get().asFile.absolutePath)
    apiPackage.set("microarch.delivery.adapters.input.http.api")
    modelPackage.set("microarch.delivery.adapters.input.http.model")
    configOptions.put("interfaceOnly", "true")
    configOptions.put("skipDefaultInterface", "true")
    configOptions.put("generateApiTests", "false")
    configOptions.put("generateModelTests", "false")
    configOptions.put("useTags", "true")
    configOptions.put("useSpringBoot3", "true")
}

tasks.withType<org.jlleitschuh.gradle.ktlint.tasks.BaseKtLintCheckTask> {
    dependsOn(tasks.openApiGenerate)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    dependsOn(tasks.openApiGenerate)
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
        freeCompilerArgs.add("-Xjsr305=strict")
    }
}

tasks.bootJar {
    archiveFileName.set("delivery.jar")
}

tasks.bootRun {
    jvmArgs("--enable-native-access=ALL-UNNAMED")
}

ktlint {
    version.set("1.5.0")
    outputToConsole.set(true)
    outputColorName.set("RED")

    filter {
        exclude { element -> element.file.path.contains("generated/") }
    }
}
