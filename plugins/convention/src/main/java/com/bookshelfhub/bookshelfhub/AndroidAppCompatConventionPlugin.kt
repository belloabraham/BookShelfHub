import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import com.bookshelfhub.bookshelfhub.libs
import com.bookshelfhub.bookshelfhub.get


class AndroidAppCompatConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {

        with(target){

            dependencies{
                "implementation"(libs["androidx.core"])
                "implementation"(libs["androidx.appcompat"])
                "implementation"(libs["google.material"])
                "implementation"(libs["androidx.constraintlayout"])
                "implementation"(libs["androidx.cardview"])
            }
        }

    }
}