plugins {
    id("bookshelfhub.android.feature")
    id ("bookshelfhub.android.navigation")
    id ("bookshelfhub.android.workmanager")

}

android {
    namespace = "com.bookshelfhub.feature.book_reviews"
}

dependencies {

    implementation(libs.firebase.bom)
    implementation(libs.firebase.firestore.ktx)

    implementation(libs.squareup.retrofit2)

    implementation(libs.github.ibrahimyilmaz)
    implementation(libs.ivbaranov.materiallettericon)
    implementation(libs.zhangai.android.materialratingbar)

    implementation(project(":core:common"))
    implementation(project(":core:authentication"))
    implementation(project(":core:model"))
    implementation(project(":core:domain"))
    implementation(project(":core:data"))
    implementation(project(":core:remote"))
    implementation(project(":core:datastore"))
    implementation(project(":core:ui"))
    implementation(project(":core:resources"))
}
