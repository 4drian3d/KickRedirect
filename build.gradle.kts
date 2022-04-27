import com.github.jengelman.gradle.plugins.shadow.tasks.ConfigureShadowRelocation

plugins {
    java
    id("net.kyori.blossom") version "1.3.0"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

repositories {
    mavenLocal()
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://repo.alessiodp.com/releases/")
    maven("https://jitpack.io")
}

dependencies {
    shadow("net.byteflux:libby-velocity:1.1.5")
    compileOnly("org.spongepowered:configurate-hocon:4.1.2")
    compileOnly("io.leangen.geantyref:geantyref:1.3.13")
    compileOnly("com.github.4drian3d:MiniPlaceholders:1.1.1")

    compileOnly("com.velocitypowered:velocity-api:3.1.2-SNAPSHOT")
    annotationProcessor("com.velocitypowered:velocity-api:3.1.2-SNAPSHOT")

    testImplementation("org.spongepowered:configurate-hocon:4.1.2")
    testImplementation(platform("org.junit:junit-bom:5.8.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("com.velocitypowered:velocity-api:3.1.2-SNAPSHOT")
    testImplementation("org.slf4j:slf4j-api:1.7.32")
}

group = "me.dreamerzero.kickredirect"
version = "2.0.0-SNAPSHOT"
description = "Set the redirect result of your servers shutdown"
val url = "https://github.com/4drian3d/KickRedirect"
val id = "kickredirect"

java.sourceCompatibility = JavaVersion.VERSION_11

blossom{
    replaceTokenIn("src/main/java/me/dreamerzero/kickredirect/utils/Constants.java")
    replaceToken("{name}", rootProject.name)
    replaceToken("{id}", id)
    replaceToken("{version}", version)
    replaceToken("{description}", description)
    replaceToken("{url}", url)
}

tasks {
    build {
        dependsOn(shadowJar)
    }
    shadowJar {
        dependsOn(getByName("relocateShadowJar") as ConfigureShadowRelocation)
        minimize()
        archiveFileName.set("KickRedirect.jar")
        configurations = listOf(project.configurations.shadow.get())
    }

    create<ConfigureShadowRelocation>("relocateShadowJar") {
        target = shadowJar.get()
        prefix = "me.dreamerzero.kickredirect.libs"
    }

    test {
        useJUnitPlatform()
        testLogging {
		    events("passed", "skipped", "failed")
	    }
    }
}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}
