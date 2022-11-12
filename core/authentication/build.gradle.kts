plugins {
    id("bookshelfhub.android.library")
}

android{
   namespace = "com.bookshelfhub.core.authentication"
}

dependencies {

    implementation(libs.dagger.hilt.android)


    //--Google Sign in auth
    implementation(libs.android.play.services.auth)

    //--Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth.ktx)

    implementation(libs.jetbrains.kotlinx.coroutines.play.services)

    implementation(project(":core:common"))
}
