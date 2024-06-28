if [ -d "deliverables" ]; then
    rm deliverables/*.jar
fi

serverFileName="server"
tuiClientFileName="clientTUI"
#guiClientFileName="clientGUI"

mvn clean compile
mvn package -Dexec.mainClass=it.polimi.ingsw.controller.Server -Djar.finalName=$serverFileName


mvn compile
mvn package -Dexec.mainClass=it.polimi.ingsw.view.cli.CLI -Djar.finalName=$tuiClientFileName


#mvn compile
#mvn package -Dexec.mainClass=it.polimi.ingsw.view.gui.GUI -Djar.finalName=$guiClientFileName


mv target/*with-dependencies.jar deliverables/
rm target/*jar