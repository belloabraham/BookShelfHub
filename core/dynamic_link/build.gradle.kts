plugins {
    id("bookshelfhub.android.library")
}

android {
    namespace = "com.bookshelfhub.core.dynamic_link"
}

dependencies {

    implementation(libs.dagger.hilt.android)

    //--Firebase
     implementation(platform(libs.firebase.bom))
     implementation(libs.firebase.dynamic.links.ktx)

    implementation(project(":core:resources"))

}
