
plugins {
    id ("bookshelfhub.android.library")
    id ("bookshelfhub.android.room")
}

android {
    namespace = "com.bookshelfhub.core.database"

}

dependencies {

    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.paging.runtime.ktx)
    implementation(project(":core:model"))
    implementation("com.google.firebase:firebase-firestore-ktx:24.4.1")

}
