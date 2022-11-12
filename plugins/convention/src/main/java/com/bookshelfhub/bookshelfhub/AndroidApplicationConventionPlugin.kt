import com.android.build.api.dsl.ApplicationExtension
import com.bookshelfhub.bookshelfhub.Config
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import com.bookshelfhub.bookshelfhub.configureAndroidAndKotlin
import com.bookshelfhub.bookshelfhub.get
import com.bookshelfhub.bookshelfhub.libs
import org.gradle.kotlin.dsl.dependencies

class AndroidApplicationConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target){
            with(pluginManager) {
                apply("com.android.application")
                apply("org.jetbrains.kotlin.android")
                apply("dagger.hilt.android.plugin")
                apply("org.jetbrains.kotlin.kapt")
            }

            extensions.configure<ApplicationExtension>{
                configureAndroidAndKotlin(this)

                buildToolsVersion = Config.BUILD_TOOLS_VERSION

                defaultConfig{
                    targetSdk = Config.TARGET_SDK
                    multiDexEnabled = true
                }

                buildTypes {
                    debug {
                        isDebuggable = true
                    }
                    release {
                        isDebuggable = false
                        isShrinkResources = true
                        multiDexEnabled = true
                    }
                }
            }

            dependencies{
                "implementation"(libs["dagger.hilt.android"])
                "kapt"(libs["dagger.hilt.compiler"])
                "kapt"(libs["androidx.hilt.compiler"])
                "implementation"(libs["androidx.core"])
                "implementation"(libs["androidx.appcompat"])
                "implementation"(libs["google.material"])
                "implementation"(libs["androidx.constraintlayout"])
                "implementation"(libs["androidx.cardview"])
            }

        }
    }
}