import com.android.build.api.dsl.LibraryExtension
import com.bookshelfhub.bookshelfhub.Config
import com.bookshelfhub.bookshelfhub.configureAndroidAndKotlin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import com.bookshelfhub.bookshelfhub.libs
import com.bookshelfhub.bookshelfhub.get
import org.gradle.kotlin.dsl.configure


class AndroidDaggerHiltConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {

        with(target){

            with(pluginManager) {
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
            }
        }

    }
}