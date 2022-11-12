plugins {
    id ("bookshelfhub.android.library")
}

android {
    namespace = "com.bookshelfhub.core.payment"
}

dependencies {

    implementation(libs.paystack)
    implementation(libs.rave.flutterwave)
    implementation(project(":core:resources"))
    implementation(project(":core:common"))
    implementation(project(":core:model"))

}

