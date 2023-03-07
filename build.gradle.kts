plugins {
    java
    alias(libs.plugins.blossom)
    alias(libs.plugins.shadow)
    alias(libs.plugins.runvelocity)
}

repositories {
    maven("https://repo.alessiodp.com/releases/") {
        mavenContent {
            includeGroup("net.byteflux")
        }
    }
    maven("https://papermc.io/repo/repository/maven-public/")
}

dependencies {
    implementation(libs.bstats)
    implementation(libs.libby)
    compileOnly(libs.configurate)
    compileOnly(libs.miniplaceholders)
    compileOnly(libs.caffeine)

    libs.velocity.run {
        compileOnly(this)
        annotationProcessor(this)
        testImplementation(this)
    }

    testImplementation(libs.configurate)
    testImplementation(platform("org.junit:junit-bom:5.9.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation(libs.assetrj)
    testImplementation(libs.slf4j)
    testImplementation(libs.caffeine)
    testImplementation(libs.bstats)
}

blossom {
    replaceTokenIn("src/main/java/io/github/_4drian3d/kickredirect/utils/Constants.java")
    replaceToken("{name}", rootProject.name)
    replaceToken("{id}", property("id"))
    replaceToken("{version}", version)
    replaceToken("{description}", description)
    replaceToken("{url}", property("url"))
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
        listOf(
            "org.spongepowered",
            "net.byteflux",
            "io.leangen.geantyref",
            "org.bstats"
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
        options.release.set(11)
    }
}

java.toolchain.languageVersion.set(JavaLanguageVersion.of(11))
