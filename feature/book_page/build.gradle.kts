plugins {
    id ("bookshelfhub.android.feature")

}

android {
  namespace = "com.bookshelfhub.book.page"
}

dependencies {
    implementation(libs.play.services.basement)

    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    implementation ("com.github.barteksc:android-pdf-viewer:3.2.0-beta.1")
    implementation(libs.jd.alexander.likeButton)
    implementation(libs.afollestad.material.dialogs.bottomsheets)


    implementation(project(":core:common"))
    implementation(project(":core:model"))
    implementation(project(":core:datastore"))
    implementation(project(":core:data"))
    implementation(project(":core:dynamic_link"))
    implementation(project(":core:authentication"))
    implementation(project(":feature:about_book"))
    implementation(project(":feature:webview"))
    implementation(project(":core:resources"))

}