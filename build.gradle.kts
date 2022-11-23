plugins {
    java
    id("net.kyori.blossom") version "1.3.0"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("xyz.jpenilla.run-velocity") version "2.0.0"
}

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://jitpack.io")
}

val configurate: String = property("configurateVersion") as String
val geantyref: String = property("geantyrefVersion") as String
val caffeine: String = property("caffeineVersion") as String

dependencies {
    implementation("com.github.AlessioDP.libby:libby-velocity:43d25ade72")
    compileOnly("org.spongepowered:configurate-hocon:$configurate")
    compileOnly("io.leangen.geantyref:geantyref:$geantyref")
    compileOnly("com.github.4drian3d:MiniPlaceholders:1.1.1")
    compileOnly("com.github.ben-manes.caffeine:caffeine:$caffeine")

    compileOnly("com.velocitypowered:velocity-api:3.1.2-SNAPSHOT")
    annotationProcessor("com.velocitypowered:velocity-api:3.1.2-SNAPSHOT")

    testImplementation("org.spongepowered:configurate-hocon:$configurate")
    testImplementation(platform("org.junit:junit-bom:5.9.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core:3.23.1")
    testImplementation("com.velocitypowered:velocity-api:3.1.2-SNAPSHOT")
    testImplementation("org.slf4j:slf4j-api:2.0.4")
    testImplementation("com.github.ben-manes.caffeine:caffeine:$caffeine")
}

blossom {
    replaceTokenIn("src/main/java/me/dreamerzero/kickredirect/utils/Constants.java")
    replaceToken("{name}", rootProject.name)
    replaceToken("{id}", property("id"))
    replaceToken("{version}", version)
    replaceToken("{description}", description)
    replaceToken("{url}", property("url"))
    replaceToken("{configurate}", configurate)
    replaceToken("{geantyref}", geantyref)
    replaceToken("{caffeine}", caffeine)
}

tasks {
    build {
        dependsOn(shadowJar)
    }

    shadowJar {
        relocate("org.spongepowered", "me.dreamerzero.kickredirect.libs.sponge")
        relocate("net.byteflux", "me.dreamerzero.kickredirect.libs.byteflux")
        relocate("io.leangen.geantyref", "me.dreamerzero.kickredirect.libs.geantyref")
        relocate("com.github.ben-manes.caffeine", "me.dreamerzero.kickredirect.libs.caffeine")
    }

    test {
        useJUnitPlatform()
        testLogging {
		    events("passed", "failed")
	    }
    }

    runVelocity {
        velocityVersion("3.1.2-SNAPSHOT")
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name()

        options.release.set(11)
    }
}

java.toolchain.languageVersion.set(JavaLanguageVersion.of(11))
