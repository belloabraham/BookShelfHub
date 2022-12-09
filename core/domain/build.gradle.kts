plugins {
    id ("bookshelfhub.android.library")
}

android {
    namespace = "com.bookshelfhub.core.domain"
}

dependencies {
    implementation(project(":core:common"))
}

