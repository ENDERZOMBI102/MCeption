pluginManagement {
	repositories {
		mavenLocal()
		mavenCentral()
		gradlePluginPortal()
		maven( url="https://maven.fabricmc.net" )
		maven( url="https://maven.quiltmc.org/repository/release" )
		maven( url="https://server.bbkr.space/artifactory/libs-release" )
	}
}
rootProject.name = "MCeption"

include( "guest" )
