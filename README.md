# iwf-java-sdk 
Java SDK for [iWF workflow engine](https://github.com/indeedeng/iwf)

See [samples](https://github.com/indeedeng/iwf-java-samples) for how to use this SDK to build your workflow. 

## Requirements 
- Java 1.8+

## Gradle
```gradle
// https://mvnrepository.com/artifact/io.iworkflow/iwf-java-sdk
implementation 'io.iworkflow:iwf-java-sdk:1.0.0-final'
```
## Maven
```
<!-- https://mvnrepository.com/artifact/io.iworkflow/iwf-java-sdk -->
<dependency>
    <groupId>io.iworkflow</groupId>
    <artifactId>iwf-java-sdk</artifactId>
    <version>1.0.0-final</version>
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
- [x] StateLocal
- [x] Signal workflow API
- [x] Get workflow DataObjects/SearchAttributes API
- [x] Get workflow API
- [x] Search workflow API
- [x] Cancel workflow API
- [x] Reset workflow API 
- [x] Command type(s) for inter-state communications (e.g. internal channel)
- [x] AnyCommandCompleted Decider trigger type
- [x] More workflow start options: IdReusePolicy, cron schedule, retry
- [x] StateOption: Start/Decide API timeout and retry policy
- [x] Reset workflow by stateId/StateExecutionId

## 1.1
- [ ] More workflow start options: initial search attributes/memo
- [ ] Decider trigger type: AnyCommandClosed
- [ ] WaitForMoreResults in StateDecision
- [ ] Skip timer API for testing/operation
- [ ] LongRunningActivityCommand
- [ ] Failing workflow details
- [ ] Auto ContinueAsNew
- [ ] StateOption: more AttributeLoadingPolicy
- [ ] StateOption: more CommandCarryOverPolicy
