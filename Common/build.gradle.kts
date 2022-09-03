plugins {
    java
}

group = "me.txmc"
version = "1.0-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
    maven { url = uri("https://repo.txmc.me/releases") }
}

dependencies {
    compileOnly("com.destroystokyo.paper:paper-jar:1.12.2-R0.1-SNAPSHOT")
    compileOnly("org.projectlombok:lombok:1.18.24")
    annotationProcessor("org.projectlombok:lombok:1.18.24")
}
