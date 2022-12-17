
package com.bookshelfhub.bookshelfhub
import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions

internal  fun Project.configureAndroidAndKotlin(extension: CommonExtension<*,*,*,*>){

    with(extension){
        compileSdk = Config.COMPILE_SDK

        lint{
            checkReleaseBuilds = false
            abortOnError = false
        }

        buildFeatures {
            viewBinding = true
        }

        defaultConfig {
           minSdk = Config.MINIMUM_SDK
           testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }

        buildTypes{
            getByName("debug"){
                isMinifyEnabled = false
                isJniDebuggable = true
            }
            getByName("release"){
                isMinifyEnabled = true
                isJniDebuggable = false
                isRenderscriptDebuggable = false
                proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            }
        }

        compileOptions{
            sourceCompatibility = Config.JAVA_VERSION
            targetCompatibility =  Config.JAVA_VERSION
            isCoreLibraryDesugaringEnabled = true
        }

        kotlinOptions{
            jvmTarget =   Config.JAVA_VERSION.toString()
            freeCompilerArgs = freeCompilerArgs + Config.FREE_COMPILER_ARGS
        }

        packagingOptions.resources.excludes.addAll(listOf(
            "META-INF/library_release.kotlin_module",
            "META-INF/LICENSE.txt",
            "META-INF/NOTICE.txt"
        ))

       with(dependencies){
           add("coreLibraryDesugaring", libs["android.desugarJdkLibs"])
           add("implementation", libs["jetbrains.kotlin.stdlib.jdk8"] )

           add("implementation", libs["kotlinx.coroutines.android"])
           add("implementation", libs["jetbrains.kotlinx.coroutines.play.services"])

           add("testImplementation", libs["junit"] )
           add("testImplementation", libs["com.google.truth"] )
           add("testImplementation", libs["org.jetbrains.kotlin.reflect"] )
           add("androidTestImplementation", libs["androidx.test.ext"] )
           add("androidTestImplementation", libs["androidx.test.espresso"] )

       }

    }

}

private fun CommonExtension<*, *, *, *>.kotlinOptions(block: KotlinJvmOptions.() -> Unit) {
    (this as ExtensionAware).extensions.configure("kotlinOptions", block)
}
