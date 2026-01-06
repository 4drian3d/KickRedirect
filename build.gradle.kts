plugins {
    java
    alias(libs.plugins.blossom)
    alias(libs.plugins.shadow)
    alias(libs.plugins.runvelocity)
    alias(libs.plugins.idea.ext)
}

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    implementation(libs.bstats)
    compileOnly(libs.miniplaceholders)

    libs.velocity.run {
        compileOnly(this)
        annotationProcessor(this)
        testImplementation(this)
    }

    testRuntimeOnly(libs.configurate)
    testImplementation(platform("org.junit:junit-bom:6.0.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation(libs.assetrj)
    testImplementation(libs.slf4j)
    testRuntimeOnly(libs.bstats)
    testImplementation(libs.mockito)
}

sourceSets {
    main {
        blossom {
            javaSources {
                property("version", project.version.toString())
            }
        }
    }
}

tasks {
    build {
        dependsOn(shadowJar)
    }

    clean {
        delete("run")
    }

    shadowJar {
        archiveBaseName.set(rootProject.name)
        archiveClassifier.set("")
        relocate("org.bstats", "io.github._4drian3d.kickredirect.libs.org.bstats")
    }

    test {
        useJUnitPlatform()
        testLogging {
            events("passed", "failed")
        }
    }

    runVelocity {
        velocityVersion(libs.versions.velocity.get())
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(21)
    }
}

java.toolchain.languageVersion.set(JavaLanguageVersion.of(21))
