# iwf-java-sdk 
WIP Java SDK for [iwf workflow engine](https://github.com/indeedeng/iwf)

## API documentation 
[API review doc](https://docs.google.com/document/d/15CETNk9ewiP7M_6N9s7jo-Wm57WG977hch9kTVnaExA/edit#) feel free to leave y our comments

## How to build & run 

### IntelliJ
1. Check out the idl submodule by running the command: `git submodule update --init --recursive`
2. In "Build, Execution, Deployment" -> "Gradle", choose "wrapper task in Gradle build script" for "Use gradle from".
3. Open Gradle tab, click "build" under "build" to build the project
4. In the same Gradle tab, click "bootRun" under "application to run the project"
5. Go to "script/http/local" folder, use the http script to invoke a REST API (you may need to install the HttpClient
   plugin for IntelliJ)

### Command lines

TODO

## Development

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
- [x] QueryAttributeRW
- [x] StateLocalAttribute
- [x] Signal workflow API
- [x] Query workflow API
- [x] Get workflow API
- [x] Search workflow API
- [x] Cancel workflow API

## 1.1

- [x] Reset workflow API (Cadence only, TODO for Temporal)
- [ ] Command type(s) for inter-state communications (e.g. internal channel)
- [ ] AnyCommandCompleted Decider trigger type
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
