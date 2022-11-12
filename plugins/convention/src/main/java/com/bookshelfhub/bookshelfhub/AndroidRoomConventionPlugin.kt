import com.bookshelfhub.bookshelfhub.libs
import com.bookshelfhub.bookshelfhub.get
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidRoomConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("org.jetbrains.kotlin.kapt")
            }

            dependencies{
                "implementation"(libs["androidx.room.runtime"])
                "implementation"(libs["androidx.room.ktx"])
                "kapt"(libs["androidx.room.compiler"])
                "implementation"(libs["androidx.room.paging"])
            }
        }
    }
}