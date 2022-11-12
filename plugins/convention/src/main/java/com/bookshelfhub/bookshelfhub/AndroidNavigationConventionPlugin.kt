import org.gradle.api.Plugin
import org.gradle.api.Project
import com.bookshelfhub.bookshelfhub.libs
import com.bookshelfhub.bookshelfhub.get
import org.gradle.kotlin.dsl.dependencies

class AndroidNavigationConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target){
            with(pluginManager) {
                apply("androidx.navigation.safeargs.kotlin")
            }

            dependencies{
                "implementation"(libs["androidx.navigation.fragment.ktx"])
                "implementation"(libs["androidx.navigation.ui.ktx"])
            }
        }
    }
}