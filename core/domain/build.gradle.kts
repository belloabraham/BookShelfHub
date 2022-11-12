plugins {
    id ("bookshelfhub.android.library")
    id ("bookshelfhub.android.workmanager")
}

android {
    namespace = "com.bookshelfhub.core.domain"

}

dependencies {
    implementation(libs.dagger.hilt.android)

    implementation(libs.firebase.bom)
    implementation(libs.firebase.storage.ktx)


    implementation(libs.androidx.lifecycle.livedata.ktx)


    implementation(project(":core:data"))
    implementation(project(":core:authentication"))
    implementation(project(":core:common"))
    implementation(project(":core:model"))
    implementation(project(":core:remote"))
}
