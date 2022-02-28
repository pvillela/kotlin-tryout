import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val kotlin_version: String by extra
buildscript {
    var kotlin_version: String by extra
    kotlin_version = "1.5.10"
    repositories {
        mavenCentral()
    }
//    dependencies {
//        classpath("org.springframework.boot:spring-boot-gradle-plugin:2.0.3.RELEASE")
//    }
}

plugins {
    application
    id("org.jetbrains.kotlin.jvm") version "1.5.10"
//    id("org.jetbrains.kotlin.plugin.spring") version "1.2.51"
//    id("org.springframework.boot") version "2.0.3.RELEASE"
//    id("io.spring.dependency-management") version "1.0.6.RELEASE"
}
apply {
    plugin("kotlin")
}

repositories {
    mavenCentral()
}

application {
    mainClassName = if (project.hasProperty("mainClass")) project.properties.get("mainClass").toString() else "examples.KotlinExampleKt"
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "11"
            freeCompilerArgs = listOf("-Xjsr305=strict")
        }
    }
}

val test by tasks.getting(Test::class) {
    useJUnitPlatform()
}

//dependencyManagement {
//    imports {
//        mavenBom("org.springframework.boot:spring-boot-dependencies:2.0.2.RELEASE")
//    }
//}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-test")

    compile("org.jetbrains.kotlinx:kotlinx-coroutines-core:${properties["coroutines_core_version"]}")
    compile("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:${properties["coroutines_jdk8"]}")
    compile("org.jetbrains.kotlinx:kotlinx-coroutines-reactive:${properties["coroutines_reactive"]}")
    compile("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:${properties["coroutines_reactor"]}")

    // The dependencyy below triggers an exception when used together with the Spring dependencies
    implementation("ch.qos.logback:logback-classic:1.0.13")

    implementation("io.projectreactor:reactor-core:3.1.8.RELEASE")

    implementation("io.projectreactor.kafka:reactor-kafka:1.0.0.RELEASE")

    implementation("com.natpryce:konfig:1.6.10.0")

    compile("com.fasterxml.jackson.module:jackson-module-kotlin:${properties["jackson_module_kotlin_version"]}")
    compile("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:${properties["jackson_datatype_jsr310_version"]}")

//    compileOnly("org.springframework.boot:spring-boot-configuration-processor")
//    compile("org.springframework.boot:spring-boot-starter")
//    compile ("org.springframework.cloud:spring-cloud-config-client:2.0.0.RELEASE")

    compile ("com.netflix.hystrix:hystrix-core:1.5.12")
    compile("io.reactivex:rxjava-reactive-streams:1.2.1")

    testImplementation("io.projectreactor:reactor-test:${properties["reactor-test"]}")
    testImplementation("org.assertj:assertj-core:${properties["assertj_core"]}")

    testImplementation("org.junit.jupiter:junit-jupiter-api:${properties["junit_jupiter_engine"]}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${properties["junit_jupiter_engine"]}")
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "11"
}

val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "11"
}
