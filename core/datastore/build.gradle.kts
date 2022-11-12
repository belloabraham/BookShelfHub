plugins {
    id ("bookshelfhub.android.library")
}

android {
    namespace = "com.bookshelfhub.core.datastore"

}

dependencies {
    implementation(libs.dagger.hilt.android)
    implementation(libs.androidx.datastore.preferences)
}
