import com.vanniktech.maven.publish.AndroidSingleVariantLibrary
import com.vanniktech.maven.publish.SonatypeHost

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.arturbosch.detekt)
    alias(libs.plugins.vanniktech.maven.publish)
    `maven-publish`
}

android {
    namespace = "io.github.suitetecsa.sdk.android"
    compileSdk = 35

    defaultConfig {

        minSdk = 26
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    lint {
        lintConfig = file("$rootDir/android-lint.xml")
        abortOnError = false
        sarifReport = true
    }

    detekt {
        buildUponDefaultConfig = true
        allRules = false
        config.setFrom(files("${rootProject.projectDir}/detekt.yml"))
        autoCorrect = true
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

android.testOptions {
    unitTests.all {
        it.useJUnitPlatform()
    }
}

dependencies {

    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    testImplementation(libs.kotest.runner.junit5)
    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.kotest.property)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation(libs.net.monster)
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()
    configure(
        AndroidSingleVariantLibrary(
            // the published variant
            variant = "release",
            // whether to publish a sources jar
            sourcesJar = true,
            // whether to publish a javadoc jar
            publishJavadocJar = true,
        )
    )

    coordinates("io.github.suitetecsa.sdk", "android", "2.0.0-beta.1")
    pom {
        name.set(project.name)
        description.set("A tool designed to interact with ETECSA services from android app.")
        url.set("https://github.com/suitetecsa/sdk-android")
        licenses {
            license {
                name.set("MIT License")
                url.set("http://github.com/suitetecsa/sdk-android/blob/master/LICENSE")
                distribution.set("repo")
            }
        }
        developers {
            developer {
                id.set("lesclaz")
                name.set("Lesly Cintra")
                email.set("lesclaz95@gmail.com")
            }
        }
        scm {
            url.set("http://github.com/suitetecsa/sdk-android/tree/master")
            connection.set("scm:git:git://github.com/suitetecsa/sdk-android.git")
            developerConnection.set("scm:git:ssh://github.com/suitetecsa/sdk-android.git")
        }
    }
}
