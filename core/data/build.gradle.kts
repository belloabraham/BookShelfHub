plugins {
    id ("bookshelfhub.android.library")
    id ("bookshelfhub.android.workmanager")

}

android {
    namespace = "com.bookshelfhub.core.data"
}

dependencies {

    implementation(libs.dagger.hilt.android)

    implementation(libs.firebase.bom)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.database.ktx)
    implementation(libs.firebase.functions.ktx)


    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.paging)

    implementation(project(":core:model"))
    implementation(project(":core:database"))
    implementation(project(":core:remote"))
    implementation(project(":core:common"))
    implementation(project(":core:datastore"))
    implementation(project(":core:payment"))
    implementation(project(":core:resources"))
    implementation(project(":core:authentication"))
    implementation(project(":core:cloud_messaging"))

}
