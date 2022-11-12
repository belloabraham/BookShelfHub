plugins {
    id("bookshelfhub.android.library")
}

android {
    namespace = "com.bookshelfhub.core.cloud.messaging"
}

dependencies {

    implementation(libs.firebase.bom)
    implementation(libs.firebase.messaging.ktx)

    implementation(project(":core:common"))
    implementation(project(":core:authentication"))
}
