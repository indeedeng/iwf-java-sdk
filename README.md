# iwf-java
Java SDK for iwf workflow engine(interpreter workflow engine for Cadence/Temporal)

## How to build & run 

### IntelliJ
1. In "Build, Execution, Deployment" -> "Gradle", choose "wrapper task in Gradle build script" for "Use gradle from".
2. Open Gradle tab, click "build" under "build" to build the project
3. In the same Gradle tab, click "bootRun" under "application to run the project"
4. Go to "script/http/local" folder, use the http script to invoke a REST API (you may need to install the HttpClient plugin for IntelliJ)

### Command lines
TODO