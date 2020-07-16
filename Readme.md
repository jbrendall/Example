# Trace Dynamo

## Build

* run 
```bash
# linux
> gradlew build

# windows
> gradle.bat build
```

## Run demos

* test driver
```bash
> gradlew runTestDriver
```

* link completion
```bash
> gradlew runLC
```

## Run tests

```
> gradlew test
```

## Logging configuration

* provide log config as environment variable 
  ```bash
  java --Dlogback.configurationFile=/path/to/config.xml <other args>
  ```
* e.g. full DEBUG logging with log file
  ```bash
  java --Dlogback.configurationFile=resources/logback-full.xml <other args>
  ```

## Link completion 

* Mainly implemented as python extension in `pythonScripts/link_completion.py`
* `link_completion.py` simply bridges (glue code) the `spojit` package, which does the heavy lifting
* `spojit` is developed in a separate repository
* Pre-bundled [wheel](https://pypi.org/project/wheel/) releases of `spojit` can be installed like any other python package
    ```bash
    pip3 install spojit-[version string].whl
    ```


 