plugins {
    id("org.jetbrains.kotlin.jvm") version "1.4.30"
    id("com.github.johnrengelman.shadow") version "6.1.0"
    application
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

application {
    mainClass.set("jenm.yagoc.Yagoc")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = "15"
}

project.setProperty("mainClassName", "jenm.yagoc.Yagoc")

tasks.register("createOSXImage", Exec::class) {
    dependsOn(tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>())
    description = "Build the OSX Image for this platform using javapackager"

    commandLine(
        listOf(
            System.getenv("JAVA_HOME") + "/bin/jpackage",
            "--name", "Yagochess",
            "--input", project.buildDir.absolutePath + "/libs",
            "--main-jar", "yagoc-desktop-all.jar",
            "--main-class", "jenm.yagoc.Yagoc",
            "--type", "app-image",
            "--java-options", "--enable-preview",
            "--dest", project.buildDir.absolutePath,
            "--license-file", "../LICENSE",
            "--description", "Yagochess is a game of chess focused on simplicity of both design and implementation.",
            "--vendor", "José Eduardo Narros Martínez",
            "--resource-dir", projectDir.absolutePath + "/src/main/resources",
            "--verbose"
        )
    )
}