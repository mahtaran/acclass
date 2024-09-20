import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
	alias(libs.plugins.spotless)

	alias(libs.plugins.android.application)
	alias(libs.plugins.kotlin.android)
	alias(libs.plugins.compose.compiler)

	id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

secrets {
	defaultPropertiesFileName = "local.defaults.properties"
}

android {
	namespace = "nl.utwente.smartspaces.acclass"
	compileSdk = 35

	defaultConfig {
		applicationId = "nl.utwente.smartspaces.acclass"
		minSdk = 34
		targetSdk = 35
		versionCode = 1
		versionName = "1.0"

		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
		vectorDrawables {
			useSupportLibrary = true
		}
	}

	buildTypes {
		release {
			isMinifyEnabled = false
			proguardFiles(
				getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
			)
		}
	}

	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_17
		targetCompatibility = JavaVersion.VERSION_17
	}

	buildFeatures {
		compose = true
		buildConfig = true
	}

	packaging {
		resources {
			excludes += "/META-INF/{AL2.0,LGPL2.1,LICENSE.md,NOTICE.md}"
		}
	}

	composeOptions {
		kotlinCompilerExtensionVersion = "1.5.1"
	}
}

kotlin {
	compilerOptions {
		jvmTarget = JvmTarget.JVM_17
	}
}

spotless {
	kotlin {
		ktfmt().googleStyle()
	}
}

dependencies {
	implementation(platform(libs.androidx.compose.bom))

	implementation(libs.androidx.core.ktx)
	implementation(libs.androidx.lifecycle.runtime.ktx)
	implementation(libs.androidx.lifecycle.viewmodel.compose)
	implementation(libs.androidx.activity.compose)
	implementation(libs.androidx.ui)
	implementation(libs.androidx.ui.graphics)
	implementation(libs.androidx.ui.tooling.preview)

	implementation(libs.androidx.material3)
	implementation(libs.accompanist.permissions)
	implementation(libs.play.services.location)
	implementation(libs.play.services.maps)
	implementation(libs.google.maps.compose)
	implementation(libs.weka) {
		exclude("com.github.vbmacher", "java-cup")
		exclude("jakarta.activation", "jakarta.activation-api")
	}

	testImplementation(libs.junit)

	androidTestImplementation(platform(libs.androidx.compose.bom))
	androidTestImplementation(libs.androidx.espresso.core)
	androidTestImplementation(libs.androidx.junit)
	androidTestImplementation(libs.androidx.ui.test.junit4)

	debugImplementation(libs.androidx.ui.tooling)
	debugImplementation(libs.androidx.ui.test.manifest)
}
