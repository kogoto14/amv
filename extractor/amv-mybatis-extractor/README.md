
```sh
git -C target clone https://github.com/gothinkster/spring-boot-realworld-example-app.git

cd target/spring-boot-realworld-example-app

jenv local 11

./gradlew assemble

# add printClasspath task to build.gradle

./gradlew -q printClasspath > classpath.txt
```

- build.gradle


```
task printClasspath {
    doLast {
        println sourceSets.main.runtimeClasspath.asPath
    }
}
```

