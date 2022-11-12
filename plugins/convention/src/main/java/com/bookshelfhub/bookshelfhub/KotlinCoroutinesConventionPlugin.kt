import com.bookshelfhub.bookshelfhub.libs
import com.bookshelfhub.bookshelfhub.get
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class KotlinCoroutinesConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target){

            dependencies{
                "implementation"(libs["kotlinx.coroutines.android"])
                "implementation"(libs["jetbrains.kotlinx.coroutines.play.services"])
            }
        }
    }
}