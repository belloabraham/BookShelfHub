plugins {
    id ("bookshelfhub.android.feature")
    id ("bookshelfhub.android.navigation")
    id ("bookshelfhub.android.workmanager")
}

android {
    namespace = "com.bookshelfhub.book.purchase"

}

dependencies {

    implementation("androidx.hilt:hilt-navigation-fragment:1.0.0")

    implementation(libs.firebase.bom)
    implementation(libs.firebase.functions.ktx)
    implementation(libs.firebase.firestore.ktx)

    implementation(libs.github.ibrahimyilmaz)
    implementation(libs.hbb20.country.picker)
    implementation(libs.paystack)

    implementation(project(":core:common"))
    implementation(project(":core:model"))
    implementation(project(":core:datastore"))
    implementation(project(":core:data"))
    implementation(project(":core:remote"))
    implementation(project(":core:resources"))
    implementation(project(":core:authentication"))
    implementation(project(":core:cloud_messaging"))
    implementation(project(":core:payment"))

}