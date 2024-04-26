package it.polimi.ingsw.distributed;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
// import java.util.concurrent.CompletableFuture;
// import java.util.concurrent.ExecutionException;
// import java.util.concurrent.ExecutorService;
// import java.util.concurrent.Executors;
// import java.util.concurrent.Future;

import it.polimi.ingsw.controller.server.Lobby;
import it.polimi.ingsw.controller.server.LobbyInfo;
import it.polimi.ingsw.controller.server.Server;
import it.polimi.ingsw.controller.server.User;
import it.polimi.ingsw.controller.server.UserInfo;

public class RMIServer extends UnicastRemoteObject implements ServerActions  {
    // ExecutorService executorService;
    
    public RMIServer() throws RemoteException {
        // executorService = Executors.newCachedThreadPool();

    }

    @Override
    public List<LobbyInfo> getLobbies() throws RemoteException {
        // CompletableFuture<List<LobbyInfo>> res = CompletableFuture.supplyAsync(() -> {
        //     return Server.INSTANCE.getLobbies();
        // }, executorService);

        // try {
        //     return res.get();
        // } catch (InterruptedException | ExecutionException e) {
        //     e.printStackTrace();
        //     return null;
        // }

        // debug return
        List<LobbyInfo> list = new java.util.ArrayList<>();
        list.add(new LobbyInfo(new Lobby(new User("Raveeee"))));
        list.add(new LobbyInfo(new Lobby(new User("Raveeee"))));
        list.add(new LobbyInfo(new Lobby(new User("Angelo"))));

        return list;

        // return Server.INSTANCE.getLobbies();
    }

    @Override
    public boolean connectToGame(User user, int lobbyId) throws RemoteException {
        return Server.INSTANCE.connectToGame(user, lobbyId);
    }

    @Override
    public boolean joinLobby(User user, int lobbyId) throws RemoteException {
        return Server.INSTANCE.joinLobby(user, lobbyId);
    }

    @Override
    public boolean leaveLobby(User user, int lobbyId) throws RemoteException {
        return Server.INSTANCE.leaveLobby(user, lobbyId);
    }

    @Override
    public UserInfo signup(String name, String password) throws RemoteException {
        return Server.INSTANCE.signup(name, password);
    }

    @Override
    public boolean startGame(User user, int lobbyId) throws RemoteException {
        return Server.INSTANCE.startGame(user, lobbyId);
    }

}