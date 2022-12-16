plugins{
    `java-gradle-plugin`
    `kotlin-dsl`
}

dependencies{
    compileOnly(libs.android.gradle)
    compileOnly(libs.kotlin.gradle)
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}


gradlePlugin{
    plugins{
        register("bookshelfhubAndroidApplication") {
            id = "bookshelfhub.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }

        register("bookshelfhubAndroidLibrary") {
            id = "bookshelfhub.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }

        register("bookshelfhubAndroidAppcompat") {
            id = "bookshelfhub.android.appcompat"
            implementationClass = "AndroidAppCompatConventionPlugin"
        }

        register("bookshelfhubAndroidNavigation") {
            id = "bookshelfhub.android.navigation"
            implementationClass = "AndroidNavigationConventionPlugin"
        }

        register("bookshelfhubAndroidWorkManager") {
            id = "bookshelfhub.android.workmanager"
            implementationClass = "AndroidWorkManagerConventionPlugin"
        }
        
        register("bookshelfhubAndroidFeature") {
            id = "bookshelfhub.android.feature"
            implementationClass = "AndroidFeatureConventionPlugin"
        }

        register("bookshelfhubAndroidRoom") {
            id = "bookshelfhub.android.room"
            implementationClass = "AndroidRoomConventionPlugin"
        }

        register("bookshelfhubAndroidViewModel") {
            id = "bookshelfhub.android.viewmodel"
            implementationClass = "AndroidViewModelConventionPlugin"
        }

        register("bookshelfhubAndroidKotlinCoroutines") {
            id = "bookshelfhub.android.kotlin.coroutines"
            implementationClass = "KotlinCoroutinesConventionPlugin"
        }

        register("bookshelfhubAndroidDaggerHilt") {
            id = "bookshelfhub.android.dagger.hilt"
            implementationClass = "AndroidDaggerHiltConventionPlugin"
        }
    }
}