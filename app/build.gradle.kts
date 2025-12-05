plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.marlodev.app_android"
    compileSdk = 36 // Usa 34 (compatible y estable con las libs nuevas)

    defaultConfig {
        applicationId = "com.marlodev.app_android"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }



    buildTypes {
        getByName("debug") {isMinifyEnabled = false

            // Mi Celular
            isMinifyEnabled = false

            // URL de desarrollo para celular en la misma red Wi-Fi comadno: ipconfig getifaddr en0
            buildConfigField("String", "BASE_URL", "\"http://192.168.0.4:8080/api/\"")
            buildConfigField("String", "WS_URL", "\"ws://192.168.0.4:8080/ws-products\"")



            // --- URL PARA DESARROLLO/LOCAL ---
////            buildConfigField("String", "BASE_URL", "\"http://10.0.2.2:8080/api/\"")
//            buildConfigField("String", "WS_URL", "\"ws://10.0.2.2:8080/ws-products\"")
            //producion
//            buildConfigField("String", "BASE_URL", "\"https://ecommerce-backend-o9y5.onrender.com/api/\"")
//            buildConfigField("String", "WS_URL", "\"wss://ecommerce-backend-o9y5.onrender.com/ws-products\"")
        }
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // --- URL PARA PRODUCCIÓN ---
//            buildConfigField("String", "BASE_URL", "\"https://ecommerce-backend-o9y5.onrender.com/api/\"")
//            buildConfigField("String", "WS_URL", "\"wss://ecommerce-backend-o9y5.onrender.com/ws-products\"")
        }
    }



    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true // ✅ Kotlin DSL
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true
        buildConfig = true   // <<< Habilita BuildConfig con tus campos personalizados

    }
}

dependencies {
    // --- MAPAS (MapLibre v12) ---
    implementation("org.maplibre.gl:android-sdk:12.0.1")

    // --- Google Play Services (solo si usas otros servicios) ---
    implementation(libs.play.services.maps)
    implementation(libs.play.services.cast.framework)


    // --- Testing ---
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // --- AndroidX + Material Design ---
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.recyclerview)
    implementation(libs.viewpager2)
    implementation("androidx.core:core-splashscreen:1.0.1")


    // --- Firebase ---
    implementation(libs.firebase.database)

    // --- Networking: Retrofit + OkHttp + Gson ---
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("com.google.code.gson:gson:2.11.0")

    // --- Glide (carga de imágenes) ---
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // --- 🔌 STOMP/WebSocket ---
    implementation("com.github.NaikSoftware:StompProtocolAndroid:1.6.6")
    implementation("io.reactivex.rxjava2:rxjava:2.2.21")
    implementation("io.reactivex.rxjava2:rxandroid:2.1.1")

    // --- UI + Navegación ---
    implementation("com.github.ismaeldivita:chip-navigation-bar:1.4.0")
    implementation("de.hdodenhof:circleimageview:3.1.0")

    // --- Lifecycle (ViewModel + LiveData) ---
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")

    // --- UI extra ---
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("com.google.android.material:material:1.12.0")

    // --- Lombok ---
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")


    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.3")

}

