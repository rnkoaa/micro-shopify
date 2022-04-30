import nu.studer.gradle.jooq.JooqGenerate
import org.gradle.internal.impldep.org.eclipse.jgit.lib.ObjectChecker.type

plugins {
    id("nu.studer.jooq") version "7.1.1"
    id("org.flywaydb.flyway") version "8.5.9"
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

configurations {

}

dependencies {
    implementation("org.jsoup:jsoup:1.14.3")

    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-jackson:2.9.0")

    implementation("com.jakewharton.retrofit:retrofit2-reactor-adapter:2.1.0")

    implementation("com.fasterxml.jackson.core:jackson-databind:2.13.2.2")
    implementation("com.fasterxml.jackson.core:jackson-core:2.13.2")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.13.2")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.13.2")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jdk8:2.13.2")

    implementation("jakarta.validation:jakarta.validation-api:3.0.1")

    // for jooq methods
    implementation("org.jetbrains:annotations:23.0.0")


    implementation("org.xerial:sqlite-jdbc:3.36.0.3")

    implementation("com.zaxxer:HikariCP:5.0.1")

    jooqGenerator("org.xerial:sqlite-jdbc:3.36.0.3")
    implementation("org.flywaydb:flyway-core:8.5.9")

    implementation("org.jooq:jooq:3.16.6")
    jooqGenerator("org.jooq:jooq-meta-extensions:3.16.6")

    implementation("org.slf4j:slf4j-api:1.7.36")

    testImplementation("org.assertj:assertj-core:3.22.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
}

flyway {
    driver = "org.sqlite.JDBC"
    mixed = true // suppress 'Detected transactional and non-transactional statements within the same migration
    url = "jdbc:sqlite:${projectDir}/src/main/resources/db/micro-shopify.db"
    locations = arrayOf("filesystem:${projectDir}/src/main/resources/db/migration")
}

jooq {
    configurations {
        create("main") {
            jooqConfiguration.apply {
                logging = org.jooq.meta.jaxb.Logging.WARN
                jdbc.apply {
                    driver = "org.sqlite.JDBC"
                    url = "jdbc:sqlite:./src/main/resources/db/micro-shopify.db"
                }
                generator.apply {
                    name = "org.jooq.codegen.DefaultGenerator"
                    database.apply {
                        name = "org.jooq.meta.sqlite.SQLiteDatabase"
                        includes = ".*"
                        excludes = ""

//                        forcedTypes = listOf(
//                            org.jooq.meta.jaxb.ForcedType().apply {
//                                userType = "com.fasterxml.jackson.databind.JsonNode"
//                                binding = "com.superstore.EventJsonBinding"
//                                includeExpression = ".*"
//                                includeTypes = ".*data.*"
//                            },
//                        )
                    }
                    target.apply {
                        packageName = "org.microshopify.jooq"
                    }
                }
            }
        }
    }
}


tasks.getByName<Test>("test") {
    dependsOn("flywayMigrateTest")
    useJUnitPlatform()
}

// by default, generateJooq will use the configured java toolchain, if any
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(18))
    }
}

tasks.named<JooqGenerate>("generateJooq") {
    dependsOn("flywayMigrate")
    // generateJooq can be configured to use a different/specific toolchain
    (launcher::set)(javaToolchains.launcherFor {
        languageVersion.set(JavaLanguageVersion.of(18))
    })
}

tasks.register<org.flywaydb.gradle.task.FlywayMigrateTask>("flywayMigrateTest") {
    description = "generate a new Db for testing"
    driver = "org.sqlite.JDBC"
    mixed = true // suppress 'Detected transactional and non-transactional statements within the same migration
    url = "jdbc:sqlite:${projectDir}/src/test/resources/db/micro-shopify.db"
    locations = arrayOf("filesystem:${projectDir}/src/main/resources/db/migration")
}

//task migrateDatabase2(type: org.flywaydb.gradle.task.FlywayMigrateTask) {
//    url = 'jdbc:h2:mem:mydb2'
//    user = 'myUsr2'
//    password = 'mySecretPwd2'
//}