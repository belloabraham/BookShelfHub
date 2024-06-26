import com.android.build.api.dsl.LibraryExtension
import com.bookshelfhub.bookshelfhub.Config
import com.bookshelfhub.bookshelfhub.configureAndroidAndKotlin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidLibraryConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target){

            with(pluginManager) {
                apply("com.android.library")
                apply("org.jetbrains.kotlin.android")
            }

            extensions.configure<LibraryExtension>{
                configureAndroidAndKotlin(this)
                defaultConfig{
                    targetSdk = Config.TARGET_SDK
                    consumerProguardFiles("consumer-rules.pro")
                }
            }

        }
    }
}