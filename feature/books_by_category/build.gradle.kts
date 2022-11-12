plugins {
    id ("bookshelfhub.android.feature")
}

android {
    namespace = "com.bookshelfhub.feature.books_by_category"
}

dependencies {

    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.paging.runtime.ktx)

    implementation(libs.github.ibrahimyilmaz)

    implementation(project(":core:data"))
    implementation(project(":core:common"))
    implementation(project(":core:resources"))
    implementation(project(":core:authentication"))
    implementation(project(":core:ui"))
    implementation(project(":core:model"))
    implementation(project(":feature:book_purchase"))
    implementation(project(":feature:book_item"))
}