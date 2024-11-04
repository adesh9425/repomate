plugins {
	id("java")
	id("org.jetbrains.kotlin.jvm") version "1.8.21"
	id("org.jetbrains.intellij") version "1.13.3"
	id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "com.repo.mate"
version = "1.0-SNAPSHOT"

repositories {
	mavenCentral()
}

intellij {
	version.set("2024.1.2")
	type.set("IC")
	plugins.set(listOf("com.intellij.java", "com.jetbrains.sh", "org.jetbrains.idea.maven", "Git4Idea"))

}

dependencies {
	implementation("org.eclipse.jgit:org.eclipse.jgit.ssh.apache:7.0.0.202409031743-r") {
		exclude(group = "org.slf4j", module = "slf4j-api")
	}

	// Provided SLF4J by IntelliJ, no need to bundle SLF4J in the plugin
	compileOnly("org.slf4j:slf4j-api:1.7.36") // Mark as provided

}

tasks {
	buildSearchableOptions {
		enabled = false
	}
	// Set the JVM compatibility versions
	withType<JavaCompile> {
		sourceCompatibility = "17"
		targetCompatibility = "17"
	}
	withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
		kotlinOptions.jvmTarget = "17"
	}

	patchPluginXml {
		sinceBuild.set("222")
		untilBuild.set("242.*")
	}

	signPlugin {
		certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
		privateKey.set(System.getenv("PRIVATE_KEY"))
		password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
	}

	publishPlugin {
		token.set(System.getenv("PUBLISH_TOKEN"))
	}

	shadowJar {
		archiveClassifier.set("")
		mergeServiceFiles()
		exclude("META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA")
	}

	build {
		dependsOn(shadowJar)
	}
}