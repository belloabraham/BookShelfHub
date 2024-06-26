import com.android.build.api.dsl.LibraryExtension
import com.bookshelfhub.bookshelfhub.Config
import com.bookshelfhub.bookshelfhub.configureAndroidAndKotlin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import com.bookshelfhub.bookshelfhub.libs
import com.bookshelfhub.bookshelfhub.get
import org.gradle.kotlin.dsl.configure

class AndroidFeatureConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {

        with(target){

            with(pluginManager) {
                apply("com.android.library")
                apply("org.jetbrains.kotlin.android")

                apply("dagger.hilt.android.plugin")
                apply("org.jetbrains.kotlin.kapt")
            }

            extensions.configure<LibraryExtension>{
                configureAndroidAndKotlin(this)
                defaultConfig{
                    targetSdk = Config.TARGET_SDK
                    consumerProguardFiles("consumer-rules.pro")
                }
            }

            dependencies{
                "implementation"(libs["dagger.hilt.android"])
                "kapt"(libs["dagger.hilt.compiler"])
                "kapt"(libs["androidx.hilt.compiler"])

                "implementation"(libs["androidx.lifecycle.viewmodel.ktx"])
                "implementation"(libs["androidx.lifecycle.livedata.ktx"])

                "implementation"(libs["androidx.fragment"])
                "implementation"(libs["androidx.core"])
                "implementation"(libs["androidx.appcompat"])
                "implementation"(libs["google.material"])
                "implementation"(libs["androidx.constraintlayout"])
                "implementation"(libs["androidx.cardview"])
            }
        }

    }
}