
plugins {
    id ("bookshelfhub.android.library")
}

android {
    namespace = "com.bookshelfhub.core.remote"

}

dependencies {

    implementation(libs.dagger.hilt.android)

    //--Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.config.ktx)
    implementation(libs.firebase.functions.ktx)
    implementation(libs.firebase.storage.ktx)
    implementation(libs.firebase.database.ktx)

    //Other Modules Dependency
    implementation(project(":core:model"))
    implementation(project(":core:common"))

    implementation(libs.squareup.retrofit2)
    implementation("com.squareup.retrofit2:converter-gson:2.5.0")

    implementation(libs.com.google.code.gson)

}
