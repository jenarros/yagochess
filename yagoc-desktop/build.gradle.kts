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
    kotlinOptions.jvmTarget = "1.8"
}

project.setProperty("mainClassName", "jenm.yagoc.Yagoc")

tasks.register("createOSXImage", Exec::class) {
    dependsOn(tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>())
    description = "Build the OSX Image for this platform using javapackager"

    commandLine(
        listOf(
            System.getenv("JAVA_HOME") + "/bin/javapackager",
            "-deploy",
            "-name", "Yagochess",
            "-title", "Yagochess (Yet Another Game Of Chess)",
            "-vendor", "José Eduardo Narros Martínez",
            "-native", "image",
            "-Bruntime=" + System.getenv("JAVA_HOME"),
            "-srcfiles", project.buildDir.absolutePath + "/libs/yagoc-desktop-all.jar",
            "-outdir", project.buildDir.absolutePath,
            "-outfile", "Yagochess",
            "-appclass", "jenm.yagoc.Yagoc",
            "-BdropinResourcesRoot=" + projectDir.absolutePath + "/src/main/resources",
            "-nosign",
            "-v"
        )
    )
}