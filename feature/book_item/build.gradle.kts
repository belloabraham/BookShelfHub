plugins {
    id ("bookshelfhub.android.feature")
    id ("bookshelfhub.android.workmanager")
}

android {
    namespace = "com.bookshelfhub.feature.book.item"
}

dependencies {

    implementation(libs.firebase.bom)
    implementation(libs.firebase.firestore.ktx)

    implementation(libs.androidx.paging.runtime.ktx)

    implementation(libs.squareup.retrofit2)


    implementation(libs.github.ibrahimyilmaz)
    implementation(libs.ivbaranov.materiallettericon)
    implementation(libs.zhangai.android.materialratingbar)

    implementation(project(":core:data"))
    implementation(project(":core:authentication"))
    implementation(project(":core:remote"))
    implementation(project(":core:model"))
    implementation(project(":core:domain"))
    implementation(project(":core:common"))
    implementation(project(":core:ui"))
    implementation(project(":feature:webview"))
    implementation(project(":core:dynamic_link"))
    implementation(project(":core:datastore"))
    implementation(project(":feature:book_reviews"))
    implementation(project(":feature:about_book"))
    implementation(project(":feature:book_page"))
    implementation(project(":feature:book_purchase"))
    implementation(project(":core:resources"))

}