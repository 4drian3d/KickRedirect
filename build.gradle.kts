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

val configurate: String = property("configurateVersion") as String
val geantyref: String = property("geantyrefVersion") as String
val caffeine: String = property("caffeineVersion") as String

dependencies {
    implementation("net.byteflux:libby-velocity:1.1.5")
    compileOnly("org.spongepowered:configurate-hocon:$configurate")
    compileOnly("io.leangen.geantyref:geantyref:$geantyref")
    compileOnly("com.github.4drian3d:MiniPlaceholders:1.1.1")
    compileOnly("com.github.ben-manes.caffeine:caffeine:$caffeine")

    compileOnly("com.velocitypowered:velocity-api:3.1.2-SNAPSHOT")
    annotationProcessor("com.velocitypowered:velocity-api:3.1.2-SNAPSHOT")

    testImplementation("org.spongepowered:configurate-hocon:$configurate")
    testImplementation(platform("org.junit:junit-bom:5.8.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("com.velocitypowered:velocity-api:3.1.2-SNAPSHOT")
    testImplementation("org.slf4j:slf4j-api:1.7.32")
    testImplementation("com.github.ben-manes.caffeine:caffeine:$caffeine")
}

java.sourceCompatibility = JavaVersion.VERSION_11

blossom{
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
		    events("passed", "skipped", "failed")
	    }
    }
}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}
