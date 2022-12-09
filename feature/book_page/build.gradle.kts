plugins {
    id ("bookshelfhub.android.feature")
    id ("bookshelfhub.android.workmanager")
}

android {
  namespace = "com.bookshelfhub.book.page"
}

dependencies {

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.storage.ktx)

    implementation("com.google.guava:guava:31.0.1-android")

    implementation(libs.play.services.basement)
    implementation(libs.androidx.lifecycle.livedata.ktx)

    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    implementation(libs.com.github.barteksc)
    implementation(libs.jd.alexander.likeButton)
    implementation(libs.afollestad.material.dialogs.bottomsheets)


    implementation(project(":core:domain"))
    implementation(project(":core:common"))
    implementation(project(":core:model"))
    implementation(project(":core:datastore"))
    implementation(project(":core:data"))
    implementation(project(":core:dynamic_link"))
    implementation(project(":core:remote"))
    implementation(project(":core:authentication"))
    implementation(project(":feature:about_book"))
    implementation(project(":feature:webview"))
    implementation(project(":core:resources"))

}