plugins {
    id("java-library")
}

group = "cn.kuzuanpa.organapi"
version = "1.1.0"

repositories {
    mavenCentral()
}

dependencies {
    // Core is pure Java: JSON parsing (Gson) + nullability annotations only.
    // Absolutely no Minecraft / Forge / NeoForge dependency (hard red line).
    api("com.google.code.gson:gson:2.10.1")
    compileOnly("org.jetbrains:annotations:24.1.0")
}

java {
    // Compiled to Java 17 so the Forge 1.20.1 target (Java 17) can consume it;
    // the NeoForge 1.21.1 target (Java 21) can consume it too.
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}
