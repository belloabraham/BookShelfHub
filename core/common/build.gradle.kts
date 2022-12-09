plugins {
    id ("bookshelfhub.android.library")
    id ("bookshelfhub.android.workmanager")
}

android {
    namespace = "com.bookshelfhub.core.common"
}

dependencies {

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics.ktx)


    //--In App Update
    implementation(libs.android.play.core)
    implementation(libs.android.play.core.ktx)

    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)


    implementation(libs.jakewharton.timber)
    implementation(libs.jakewharton.threetenabp)
    implementation(libs.apache.commons.text)
    implementation(libs.com.squareup.picasso)
    implementation(libs.com.google.code.gson)

    implementation(libs.klinkerapps.link.builder)
    implementation(libs.grenderg.toasty)
    implementation(libs.skydoves.balloon)
    implementation(libs.afollestad.material.dialogs.bottomsheets)
    implementation(libs.afollestad.material.dialogs.lifecycle)

    implementation(libs.play.services.base)

    implementation(project(":core:resources"))

}
