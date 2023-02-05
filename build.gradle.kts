plugins {
    java
    alias(libs.plugins.blossom)
    alias(libs.plugins.shadow)
    alias(libs.plugins.runvelocity)
}

repositories {
    mavenLocal()
    maven("https://papermc.io/repo/repository/maven-public/") {
        mavenContent {
            includeGroup("com.velocitypowered")
        }
    }
    mavenCentral()
    maven("https://jitpack.io") {
        mavenContent {
            includeGroup("com.github.AlessioDP.libby")
            includeGroup("com.github.4drian3d")
        }
    }
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
    testImplementation(platform("org.junit:junit-bom:5.9.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation(libs.assetrj)
    testImplementation(libs.slf4j)
    testImplementation(libs.caffeine)
    testImplementation(libs.bstats)
}

blossom {
    replaceTokenIn("src/main/java/me/dreamerzero/kickredirect/utils/Constants.java")
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
        relocate("org.spongepowered", "me.dreamerzero.kickredirect.libs.sponge")
        relocate("net.byteflux", "me.dreamerzero.kickredirect.libs.byteflux")
        relocate("io.leangen.geantyref", "me.dreamerzero.kickredirect.libs.geantyref")
        relocate("org.bstats", "me.dreamerzero.kickredirect.libs.bstats")

        // TODO: Apply in a future release
        /*listOf(
            "org.spongepowered",
            "net.byteflux",
            "io.leangen.geantyref",
            "org.bstats"
        ).forEach {
            relocate(it, "me.adrianed.kickredirect.libs.$it")
        }*/
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
