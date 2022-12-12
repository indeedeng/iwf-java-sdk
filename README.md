# iwf-java-sdk 
Java SDK for [iWF workflow engine](https://github.com/indeedeng/iwf)

### Gradle
```gradle
// https://mvnrepository.com/artifact/io.iworkflow/iwf-java-sdk
implementation 'io.iworkflow:iwf-java-sdk:1.0.0-rc1'
```
### Maven
```
<!-- https://mvnrepository.com/artifact/io.iworkflow/iwf-java-sdk -->
<dependency>
    <groupId>io.iworkflow</groupId>
    <artifactId>iwf-java-sdk</artifactId>
    <version>1.0.0-rc1</version>
    <type>pom</type>
</dependency>

```

## Concepts

To implement a workflow, the two most core interfaces are

* [Workflow interface](https://github.com/indeedeng/iwf-java-sdk/blob/main/src/main/java/io/iworkflow/core/Workflow.java)
  defines the workflow definition

* [WorkflowState interface](https://github.com/indeedeng/iwf-java-sdk/blob/main/src/main/java/io/iworkflow/core/WorkflowState.java)
  defines the workflow states for workflow definitions

A workflow can contain any number of WorkflowStates.

See more in https://github.com/indeedeng/iwf#what-is-iwf

## How to build & run

### Using IntelliJ

1. Check out the idl submodule by running the command: `git submodule update --init --recursive`
2. In "Build, Execution, Deployment" -> "Gradle", choose "wrapper task in Gradle build script" for "Use gradle from".
3. Open Gradle tab, click "build" under "build" to build the project
4. In the same Gradle tab, click "bootRun" under "application to run the project"
5. Go to "script/http/local" folder, use the http script to invoke a REST API (you may need to install the HttpClient
   plugin for IntelliJ)

## Development Guide

### Update IDL

Run the command `git submodule update --remote --merge` to update IDL to the latest commit

# Development Plan

## 1.0

- [x] Start workflow API
- [x] Executing `start`/`decide` APIs and completing workflow
- [x] Parallel execution of multiple states
- [x] Timer command
- [x] Signal command
- [x] SearchAttributeRW
- [x] DataObjectRW
- [x] StateLocalAttribute
- [x] Signal workflow API
- [x] Get workflow DataObjects/SearchAttributes API
- [x] Get workflow API
- [x] Search workflow API
- [x] Cancel workflow API

## 1.1

- [x] Reset workflow API (Cadence only, TODO for Temporal)
- [x] Command type(s) for inter-state communications (e.g. internal channel)
- [x] AnyCommandCompleted Decider trigger type
- [ ] More workflow start options: IdReusePolicy, initial earch attributes, cron schedule, retry, etc
- [ ] StateOption: Start/Decide API timeout and retry
- [ ] Reset workflow by stateId

## 1.2

- [ ] Decider trigger type: AnyCommandClosed
- [ ] WaitForMoreResults in StateDecision
- [ ] Skip timer API for testing/operation
- [ ] LongRunningActivityCommand
- [ ] Failing workflow details
- [ ] Auto ContinueAsNew
- [ ] StateOption: more AttributeLoadingPolicy
- [ ] StateOption: more CommandCarryOverPolicy
