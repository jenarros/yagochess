plugins {
    id("org.jetbrains.kotlin.jvm") version "1.4.30"
    id("com.github.johnrengelman.shadow") version "6.1.0"
    java
}

repositories {
    jcenter()
}

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.junit.jupiter:junit-jupiter:5.7.1")
    testImplementation("org.hamcrest:hamcrest:2.2")
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

tasks.withType<Jar>().configureEach {
    manifest {
        attributes["Main-Class"] = "jenm.yagoc.Yagoc"
        attributes[project.name + "-version"] = project.version
        attributes[project.name + "-sha"] = project.property("sha")
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = "11"
}

tasks.register("createOSXImage", Exec::class) {
    dependsOn(tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>())
    description = "Build the OSX DMG package."

    commandLine(
        listOf(
            System.getenv("JAVA_HOME") + "/bin/jpackage",
            "--name", "yagochess",
            "--input", project.buildDir.absolutePath + "/libs",
            "--main-jar", "yagoc-desktop-" + project.version + "-all.jar",
            "--main-class", "jenm.yagoc.Yagoc",
            "--add-modules", "java.desktop",
            "--type", "dmg",
            "--java-options", "--enable-preview",
            "--dest", project.buildDir.absolutePath + "/distributions",
            "--license-file", "../LICENSE",
            "--description", "Yagochess is a game of chess focused on simplicity of both design and implementation.",
            "--vendor", "José Eduardo Narros Martínez",
            "--app-version", project.version,
            "--resource-dir", projectDir.absolutePath + "/src/main/resources",
            "--verbose"
        )
    )
}