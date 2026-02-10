plugins {
    id("com.android.application")
    // Kotlin 插件已经在你的根目录被应用了，这里不需要再写，否则会报“重复添加”错误
}

android {
    namespace = "com.shiweicao.pinisland"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.shiweicao.pinisland"
        minSdk = 36
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    // 删除 kotlinOptions 块，解决 "Unresolved reference" 报错。
    // 默认配置足够运行你的 App。
}

dependencies {
    // 使用稳定版本的硬编码依赖，确保一定能下载成功
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.activity:activity:1.8.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
}