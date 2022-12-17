
plugins {
    id ("bookshelfhub.android.feature")
    id ("bookshelfhub.android.navigation")
    id ("bookshelfhub.android.workmanager")
}

android {

    namespace = "com.bookshelfhub.feature.home"

    buildFeatures {
        dataBinding = true
    }
}

dependencies {

    implementation(libs.firebase.bom)
    implementation(libs.firebase.firestore.ktx)

    //--In App Update
    implementation(libs.android.play.core)
    implementation(libs.android.play.core.ktx)

    implementation(libs.androidx.paging.runtime.ktx)
    implementation(libs.androidx.work.runtime.ktx)

    implementation("com.jakewharton:process-phoenix:2.1.2")

    implementation(libs.github.ibrahimyilmaz)
    implementation(libs.joery.animatedbottombar)
    implementation(libs.bitvale.switcher)
    implementation(libs.androidx.swiperefreshlayout)
    implementation(libs.afollestad.material.dialogs.bottomsheets)


    implementation(project(":core:data"))
    implementation(project(":core:authentication"))
    implementation(project(":core:cloud_messaging"))
    implementation(project(":core:remote"))
    implementation(project(":core:dynamic_link"))
    implementation(project(":core:common"))
    implementation(project(":core:domain"))
    implementation(project(":core:datastore"))
    implementation(project(":core:database"))
    implementation(project(":core:resources"))
    implementation(project(":feature:books_by_category"))
    implementation(project(":feature:book_page"))
    implementation(project(":feature:webview"))
    implementation(project(":feature:book_item"))
    implementation(project(":feature:book_purchase"))
    implementation(project(":core:ui"))
    implementation(project(":core:model"))

}