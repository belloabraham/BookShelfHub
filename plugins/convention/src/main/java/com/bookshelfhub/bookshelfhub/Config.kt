package com.bookshelfhub.bookshelfhub

import org.gradle.api.JavaVersion

object Config {
    const val MINIMUM_SDK = 21
    const val TARGET_SDK = 33
    const val  COMPILE_SDK = 33
    const val BUILD_TOOLS_VERSION = "30.0.3"
    val JAVA_VERSION = JavaVersion.VERSION_11
    val FREE_COMPILER_ARGS = listOf("-opt-in=kotlin.RequiresOptIn")
}