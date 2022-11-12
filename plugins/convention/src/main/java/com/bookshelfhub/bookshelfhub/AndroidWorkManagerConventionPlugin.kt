import com.bookshelfhub.bookshelfhub.libs
import com.bookshelfhub.bookshelfhub.get
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidWorkManagerConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            dependencies{
                "implementation"(libs["androidx-work-runtime-ktx"])
                "implementation"(libs["androidx.hilt.work"])
            }
        }
    }
}