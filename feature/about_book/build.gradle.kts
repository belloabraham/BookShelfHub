plugins {
    id("bookshelfhub.android.feature")
    id ("bookshelfhub.android.navigation")
}

android {
    namespace = "com.bookshelfhub.feature.about.book"
}

dependencies {

    implementation(libs.klinkerapps.link.builder)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore.ktx)

    implementation(project(":core:common"))
    implementation(project(":core:model"))
    implementation(project(":core:domain"))
    implementation(project(":core:data"))
    implementation(project(":core:resources"))

}