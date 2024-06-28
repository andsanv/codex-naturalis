# Codex Naturalis - Software Engineering Project

<img src="src/main/resources/images/bg_menu.png" width=192px height=192px align="right"  alt="Codex Naturalis Logo"/>

Codex Naturalis project as final test of **"Software Engineering"** course of **"Computer Science Engineering"** at Politecnico di Milano (2023/2024). <br />

**Professor**: [Gianpaolo Cugola](https://cugola.faculty.polimi.it/)

**Group**: GC-11

**The team**: 
- [Sameuele Pischedda](https://github.com/spische)
- [Angelo Prete](https://github.com/angpre)
- [Gabriele Raveggi](https://github.com/raveeee0)
- [Andrea Sanvito](https://github.com/andsanv)

## Project info
The project is the software (Java) version of the game Codex Naturalis

The project includes:
- High level uml sequence diagrams;
- High level uml diagram;
- Complete uml diagram;
- Peer Reviews of model and network;
- Source code of the game implementation;
- Source code of junit tests;

## Implemented Functionalities

| Functionality               | Status             |
|-----------------------------|--------------------|
| Basic Rules                 | :heavy_check_mark: |
| Complete Rules              | :heavy_check_mark:  |
| TUI                         | :heavy_check_mark: |
| Socket                      | :heavy_check_mark: |
| GUI                         | :heavy_check_mark: |
| RMI                         | :heavy_check_mark: |
| Multiple matches (FA 1)     | :heavy_check_mark: |
| Chats (FA 2)                | :heavy_check_mark: (TUI only)|

## Testing

Almost all `model` and `controller` classes have a class and method coverage of 100%.

**Coverage**: code lines covered.

| Package    | Class          | Coverage      |
|------------|----------------|---------------|
| Model      | Entire Package | 100% |
| Controller | Entire Package | 95%  |
| Controller | GameFlowManager  | 88% |
| Controller | GameModelUpdater     | 100%  |

To run the tests, after setting the options (2 different in the pom.xml) 'skip tests' to false:
```bash
mvn test
```

## Requirements

To run the test and compile the software:

1. Install [Java SE Development Kit 21](https://docs.oracle.com/en/java/javase/21/)
2. Install [Maven](https://maven.apache.org/install.html)
3. Clone this repo by downloading the `.zip` and extract it, or using the `git clone` command.

## Run using the JAR files
Once installed all the requirements, open a terminal and run ``` ./buildjar.sh```, then go to the deliverable/ older.
Start the server and decide to start the cli or gu:

### Start the Server
```bash
java -jar deliverables/server-jar-with-dependencies.jar [--server-ip=<SERVER_IP>] [--socket-port=<SOCKET_PORT>] [--rmi-port=<RMI_PORT>]
```

### Run the Client (TUI)
```bash
java -jar deliverables/clientTUI-jar-with-dependencies.jar [--server-ip=<SERVER_IP>] [--socket-port=<SOCKET_PORT>] [--rmi-port=<RMI_PORT>]
```

### Run the Client (GUI)
To run the GUI:
```bash
mvn javafx:run
```
If not playing on localhost the Config.java file should be changed manually to select the network options.

The fields are optional:
- SERVER_IP is the private ip of the server machine, 127.0.0.1 by default to play on the same machine (both client and server). 
- SOCKET_PORT is the port where the socket server listens, by deafult random
- RMI_PORT is the port where the rmi server listens, by default random


#### More:
To play the client should support UTF-8 encoding, ANSI escape characters and it is also advised to play in full screen.