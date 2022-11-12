plugins {
    id ("bookshelfhub.android.feature")
}

android {
    namespace = "com.bookshelfhub.feature.webview"
}

dependencies {
    implementation(project(":core:common"))
}