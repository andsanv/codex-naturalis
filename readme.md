# Eriantys - Software Engineering Project

<img src="" width=192px height=192px align="right"  alt="Codex Naturalis Logo"/>

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
- Compiled JAR files;

## Implemented Functionalities

| Functionality               | Status             |
|-----------------------------|--------------------|
| Basic Rules                 | :heavy_check_mark: |
| Complete Rules              | :heavy_check_mark: |
| TUI                         | :heavy_check_mark: |
| GUI                         | :heavy_check_mark: |
| Socket                      | :heavy_check_mark: |
| RMI                         | :heavy_check_mark: |
| Multiple matches (FA 1)     | :heavy_check_mark: |
| Chats (FA 2)                | :heavy_check_mark: |
<!--| Multiple Matches (FA 3)     | :heavy_check_mark: |-->

## Testing

Almost all `model` and `controller` classes have a class and method coverage of 100%.

**Coverage**: code lines covered.

| Package    | Class          | Coverage      |
|------------|----------------|---------------|
| Model      | Entire Package | 95% (109/114) |
| Controller | Entire Package | 82% (250/303) |
| Controller | ClientHandler  | 69% (53/76)   |
| Controller | Controller     | 90% (166/184) |
| Controller | Server         | 72% (31/43)   | 


## Compile

To run the test and compile the software:

1. Install [Java SE Development Kit 18](https://docs.oracle.com/en/java/javase/18/)
2. Install [Maven](https://maven.apache.org/install.html)
3. Clone this repo by either downloading the `.zip` and extract it, or using the `git clone` command.
4. Open a terminal, navigate to the project folder and compile sources of the package:
    ```bash
    cd /path/to/project/home/directory
    mvn clean package
    ```

## Run using the JAR file
Once installed all requirements and compiled the project, open a terminal and
go to the project target directory. 
Once there it is possible to choose to run the server or the client (CLI or GUI):

### Run the Server
```bash
java -jar Eriantys.jar -s
```
or
```bash
java -jar Eriantys.jar --server
```
### Run the Client (CLI)
```bash
java -jar Eriantys.jar -c
```
or
```bash
java -jar Eriantys.jar --cli
```

### Run the Client (GUI)
```bash
java -jar Eriantys.jar
```
To run the Client (GUI) it is also possible to open the JAR file directly from
the file explorer.

### Recommendations

In order to play, you'll have to launch at least one server and two clients (either CLI or GUI).

**WARNING**: For the best GUI experience it is strongly suggested to play with a screen resolution
of 1920x1080 (100% DPI) and with a scaling of 100%.

**WARNING**: For the best CLI experience it is strongly suggested to play with the terminal in fullscreen mode. Zoom out and refresh if it's not visible at the beginning.
Based on the system setting, zooming out/in the terminal could improve the appearance of the game.