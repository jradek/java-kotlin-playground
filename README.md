# Running and debuging a mixed java/kotlin project with VS code

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

## Debugging

Debug settings in `.vscode/lauch.json`

```json
{
    "version": "0.2.0",
    "configurations": [
        {
            "type": "kotlin",
            "request": "launch",
            "name": "Kotlin Launch",
            "projectRoot": "${workspaceFolder}",
            "mainClass": "sample/app/AppKt",
            "preLaunchTask": "build"
        },
        {
            "type": "java",
            "request": "launch",
            "name": "Java Launch",
            "projectRoot": "${workspaceFolder}",
            "mainClass": "sample.app.Main",
            "preLaunchTask": "build"
        }
    ]
}
```
