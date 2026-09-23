plugins {
    id("net.neoforged.moddev")
}

version = "${property("mod.version")}-${property("deps.minecraft")}-neoforge"
base.archivesName = property("mod.id") as String
group = property("mod.group") as String

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://maven.parchmentmc.org") { name = "ParchmentMC" }
}

dependencies {
    // Pure-Java core module; its classes are folded into the mod jar and the
    // compat devJar so downstream consumers see one artifact.
    implementation(project(":organ-core"))
}

neoForge {
    version = property("deps.neoforge") as String
    validateAccessTransformers = true

    if (hasProperty("deps.parchment")) parchment {
        val (mc, ver) = (property("deps.parchment") as String).split(':')
        mappingsVersion = ver
        minecraftVersion = mc
    }

    runs {
        register("client") {
            gameDirectory = file("run/")
            client()
        }
        register("server") {
            gameDirectory = file("run/")
            server()
        }
    }

    mods {
        register(property("mod.id") as String) {
            sourceSet(sourceSets["main"])
        }
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

tasks {
    named("createMinecraftArtifacts") {
        dependsOn("stonecutterGenerate")
    }

    processResources {
        // The Forge manifest is not consumed by NeoForge; only expand ours.
        exclude("META-INF/mods.toml")
        val props = mapOf(
            "mod_id" to project.property("mod.id"),
            "mod_name" to project.property("mod.name"),
            "mod_license" to project.property("mod.license"),
            "mod_version" to project.property("mod.version"),
            "mod_authors" to project.property("mod.authors"),
            "mod_description" to project.property("mod.description"),
            "minecraft_version" to project.property("deps.minecraft"),
            "minecraft_version_range" to project.property("deps.minecraft_version_range"),
            "neoforge_version" to project.property("deps.neoforge"),
            "neoforge_version_range" to project.property("deps.neoforge_version_range"),
            "loader_version_range" to project.property("deps.loader_version_range"),
            "pack_format" to "34",
        )
        inputs.properties(props)
        filesMatching(listOf("META-INF/neoforge.mods.toml", "pack.mcmeta")) {
            expand(props)
        }
    }

    jar {
        // Fold the pure-Java core classes into the mod jar so it is a single,
        // self-contained artifact at runtime (Gson is already on the MC classpath).
        from(project(":organ-core").sourceSets["main"].output)
        // NeoForge uses official mojmap; there is no reobf step.
    }
}

// Compatibility dev jar consumed by downstream mods; lands in the version
// subproject build/libs. NeoForge downstream is not migrated yet (step 6), so
// this exists mainly for parity with the forge target's contract.
tasks.register<Jar>("devJar") {
    archiveClassifier = "dev"
    archiveVersion = project.property("mod.version") as String
    from(sourceSets["main"].output)
    from(project(":organ-core").sourceSets["main"].output)
    dependsOn("classes", ":organ-core:classes")
}

tasks.named("build") {
    dependsOn("devJar")
}
