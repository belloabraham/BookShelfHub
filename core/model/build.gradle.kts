plugins {
    id ("bookshelfhub.android.library")
}

android {
    namespace = "com.bookshelfhub.core.model"
}

dependencies {

    implementation("com.google.firebase:firebase-firestore-ktx:24.4.1")
    implementation("com.google.firebase:firebase-database-ktx:20.1.0")
    implementation(libs.androidx.room.ktx)
}
