package it.polimi.ingsw.distributed;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ClientTest {
    public static void main(String[] args) throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry(60200);

        ServerActions stub = (ServerActions) registry.lookup("ServerCodex");

        stub.getLobbies()
            .stream()
            .flatMap(lobby -> lobby.users.stream())
            .forEach(System.out::println);
        
    }
    
}