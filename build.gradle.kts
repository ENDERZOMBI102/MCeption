@file:Suppress("SpellCheckingInspection", "UnstableApiUsage")
plugins {
	id("com.github.johnrengelman.shadow") version "7.1.2"
	id("org.quiltmc.loom") version "1.0.+"
	kotlin("jvm") version "1.8.0"
}

val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

/** Utility function that retrieves a bundle from the version catalog. */
fun bundle( bundleName: String ) =
	libs.findBundle( bundleName ).get()

allprojects {
	apply( plugin="org.quiltmc.loom" )
	apply( plugin="org.jetbrains.kotlin.jvm" )
	loom.runtimeOnlyLog4j.set( true )

	repositories {
		mavenCentral()
		maven( "https://jitpack.io" )
		maven( "https://maven.gegy.dev" )
		maven( "https://maven.shedaniel.me" )
		maven( "https://maven.ryanliptak.com" )
		maven( "https://repsy.io/mvn/enderzombi102/mc" )
		maven( "https://maven.terraformersmc.com/releases" )
	}

	dependencies {
		implementation( bundle( "implementation" ) )
		modImplementation( bundle( "modImplementation" ) )
	}

	tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
		kotlinOptions.jvmTarget = "1.8"
	}

	tasks.withType<JavaCompile> {
		options.encoding = "UTF-8"
		options.release.set( 8 )
	}

	java.toolchain.languageVersion.set( JavaLanguageVersion.of( 8 ) )
}

val shade = configurations.create( "shade" )
val loaderVersion = libs.findVersion("loader").get().displayName
val minecraftVersion = "1.19.2"
val mappingsVersion = "21"

loom.runConfigs["client"].isIdeConfigGenerated = true
loom.runConfigs["server"].isIdeConfigGenerated = true

dependencies {
	minecraft( "com.mojang:minecraft:$minecraftVersion" )
	mappings( "org.quiltmc:quilt-mappings:$minecraftVersion+build.$mappingsVersion:intermediary-v2" )
}

tasks.withType<ProcessResources> {
	inputs.property( "group"            , project.group )
	inputs.property( "version"          , version )
	inputs.property( "loader_version"   , loaderVersion )
	inputs.property( "minecraft_version", minecraftVersion )
	filteringCharset = "UTF-8"

	filesMatching( "quilt.mod.json" ) {
		expand(
			"version"      to version,
			"group"        to project.group,
			"dependencies" to """
				{ "id": "quilt_loader", "versions": "$loaderVersion" },
				{ "id": "quilt_base", "versions": "*" },
				{ "id": "minecraft", "versions": ">=$minecraftVersion" },
				{ "id": "java", "versions": ">=17" }""".trimIndent()
		)
		filter { it.substringBefore("///") }
	}
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
	configurations = listOf( shade )
	from( "LICENSE" ) {
		rename { "${it}_$archiveBaseName}" }
	}
}

