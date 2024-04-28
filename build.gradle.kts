fun properties(key: String) = providers.gradleProperty(key)

plugins {
  id("java")
  id("org.jetbrains.kotlin.jvm") version "1.7.20"
  id("org.jetbrains.intellij") version "1.13.1"
}

group = properties("group").get()
version = properties("version").get()

repositories {
  mavenCentral()
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
  version.set(properties("platformVersion").get())
  type.set(properties("platformType").get()) // Target IDE Platform
  plugins.set(properties("platformPlugins").map { it.split(',').map(String::trim).filter(String::isNotEmpty) })
}

tasks {
  // Set the JVM compatibility versions
  withType<JavaCompile> {
    sourceCompatibility = properties("javaVersion").get()
    targetCompatibility = properties("javaVersion").get()
  }
  withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = properties("javaVersion").get()
  }

  patchPluginXml {
    sinceBuild.set(properties("pluginSinceBuild").get())
    untilBuild.set(properties("pluginUntilBuild").get())
  }

  signPlugin {
    certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
    privateKey.set(System.getenv("PRIVATE_KEY"))
    password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
  }

  publishPlugin {
    token.set(System.getenv("PUBLISH_TOKEN"))
  }
}
