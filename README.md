RoboRally.

RoboRally is a strategical boardgame, where players uses their Robots to navigate through a bord filled with dangers and obstacles in a specific order. This game implements a graphic version of Roborally where code and graphical user interface are developed in java and javafx. This game was developed by a group to challenge skills in software development.

This game is developed using intellij idea. 
The code is written in java and javafx language and it uses maven to deal with dependencies.
Here is a what-to-do-list in order to preprare a computer to run the game:

1. Install java JDK at least version 19. This link can be used: https://www.oracle.com/java/technologies/javase/jdk19-archive-downloads.html
2. Install intelliJ - highly recommended as the game is developed in intelliJ idea. This link can be used: https://www.jetbrains.com/idea/download/?section=windows

Afterwards the game requires these Maven-dependencies that can automatically be downloaded and setup via the "pom.xml":

com.google.code.gson:gson:2.8.2

com.google.guava:guava:19.0

com.google.protobuf:protobuf-java:2.6.0

mysql:mysql-connector-java:8.0.11

org.jetbrains:annotations:13.0

org.junit.jupiter:junit-jupiter-api:5.4.2

org.openjfx 

org.opentest4j:opentest4j:1.1.1

Then the game should be imported as a project in intellij. Before proceeding the guide the user need to know the direct path to the folder where this game is saved on the computer.
1. open IntelliJ IDEA.
2. Choose 'File' > 'Open' and navigate to the path of the folder the game is saved in.
3. IntelliJ should automatically recognise the `pom.xml` file and setup the required Maven-dependencies. If this does not happen, go to "file" -> "Project Structure" -> "Modules" and add Maven.
4. wait while IntelliJ downloads and setup the dependecies.

Remember to control your configuration to ensure that javafx is correctly sat up, especially if the user uses Windows, though the user have to include the module specifically to the users operating system (`javafx-base:win`, `javafx-controls:win`, `javafx-graphics:win`).

If the previous steps are performed the user is now ready to run the game.

Click on the play button close to the upper right corner and a window will popup.
Choose "File" -> "New game" and select the amount of players that are going to participate in the game and select "defaultboard". The "defaultboard" is made with 8 rows and 8 columns of squares. Then choose a position where each player want to start.



