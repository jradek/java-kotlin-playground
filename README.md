# Playing with java and kotlin in one project

It supports running and debuging a mixed java/kotlin project with VS code or IntelliJ

## Building

```console
gradle build
```

This creates a jar and collects all runtime dependencies in `build/libs`.
An "uberJar" containing all dependencies is built in `build/shadowJar`.

## Running

* java Main

    ```console
    java -cp "build/libs/*" sample.app.Main
    ```

* kotlin Main

    ```console
    java -cp "build/libs/*" sample.app.AppKt
    ```
