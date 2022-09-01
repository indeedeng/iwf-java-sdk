# iwf-java
Java SDK for iwf workflow engine(interpreter workflow engine for Cadence/Temporal)

## How to build & run 

### IntelliJ
1. Check out the idl submodule by running the command: `git submodule update --init --recursive`
2. In "Build, Execution, Deployment" -> "Gradle", choose "wrapper task in Gradle build script" for "Use gradle from".
3. Open Gradle tab, click "build" under "build" to build the project
4. In the same Gradle tab, click "bootRun" under "application to run the project"
5. Go to "script/http/local" folder, use the http script to invoke a REST API (you may need to install the HttpClient plugin for IntelliJ)

### Command lines
TODO

## Development

### Update IDL
Run the command `git submodule update --remote --merge` to update IDL to the latest commit