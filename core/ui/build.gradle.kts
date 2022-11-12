plugins {
    id ("bookshelfhub.android.library")
    id ("bookshelfhub.android.appcompat")
    id ("kotlin-android-extensions")
}

android {
  namespace = "com.bookshelfhub.core.ui"
}

dependencies {
    implementation(libs.github.ibrahimyilmaz)

    implementation(project(":core:model"))
    implementation(project(":core:common"))
    implementation(project(":core:resources"))
    implementation(libs.androidx.recycler)
}
