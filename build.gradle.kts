plugins {
    java
    alias(libs.plugins.blossom)
    alias(libs.plugins.shadow)
    alias(libs.plugins.runvelocity)
}

repositories {
    maven("https://maven.deltapvp.net/") {
        mavenContent {
            includeGroup("net.deltapvp.libby")
        }
    }
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    implementation(libs.bstats)
    implementation(libs.libby)
    implementation(libs.hexlogger)
    compileOnly(libs.configurate)
    compileOnly(libs.miniplaceholders)

    libs.velocity.run {
        compileOnly(this)
        annotationProcessor(this)
        testImplementation(this)
    }

    testRuntimeOnly(libs.configurate)
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation(libs.assetrj)
    testImplementation(libs.slf4j)
    testRuntimeOnly(libs.bstats)
    testImplementation(libs.mockito)
}

blossom {
    replaceTokenIn("src/main/java/io/github/_4drian3d/kickredirect/utils/Constants.java")
    replaceToken("{version}", version)
    replaceToken("{configurate}", libs.versions.configurate.get())
    replaceToken("{geantyref}", libs.versions.geantyref.get())
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
        listOf(
            "org.spongepowered",
            "net.byteflux",
            "io.leangen.geantyref",
            "org.bstats",
            "io.github._4drian3d.velocityhexlogger",
            "net.kyori.adventure.text.logger.slf4j"
        ).forEach {
            relocate(it, "io.github._4drian3d.kickredirect.libs.$it")
        }
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
        options.release.set(17)
    }
}

java.toolchain.languageVersion.set(JavaLanguageVersion.of(17))
