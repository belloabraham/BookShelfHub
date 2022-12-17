plugins {
    id ("bookshelfhub.android.feature")
    id ("bookshelfhub.android.navigation")
}

android {
    namespace = "com.bookshelfhub.feature.onboarding"
}

dependencies {

    //--Google Sign in auth
    implementation(libs.android.play.services.auth)

    //--Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth.ktx)


    implementation(libs.klinkerapps.link.builder)
    implementation(libs.smarteist.autoimageslider)
    implementation(libs.aabhasr1.otpView)
    implementation(libs.airbnb.lottie)
    implementation(libs.hbb20.cpp)
    implementation(libs.skydoves.balloon)

    implementation(project(":core:data"))
    implementation(project(":core:dynamic_link"))
    implementation(project(":core:authentication"))
    implementation(project(":core:common"))
    implementation(project(":core:payment"))
    implementation(project(":core:resources"))
    implementation(project(":core:model"))
    implementation(project(":core:domain"))
    implementation(project(":feature:home"))
    implementation(project(":core:datastore"))

}