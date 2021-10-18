package it.fulminazzo.sbcAPI;

import it.fulminazzo.sbcb.StrippedBroadcastBungee;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlayersUtilB {
    /*
      This class will be used by the Bungeecord plugin, and
      it will be available for the API to get a
      list of players according to the given
      input.
     */

    /**
     * @return a list of all players in the server.
     */
    public static List<ProxiedPlayer> getAllPlayers() {
        return new ArrayList<>(ProxyServer.getInstance().getPlayers());
    }

    /**
     * If the sender is console, then the result will be null.
     * Otherwise, it will be the player who executed the command.
     *
     * @param sender: the issuer of the command.
     * @return a list containing the player.
     */
    public static List<ProxiedPlayer> getUserPlayer(CommandSender sender) {
        return (sender instanceof ProxiedPlayer) ? new ArrayList<>(Collections.singleton((ProxiedPlayer) sender)) : null;
    }

    /**
     * @param playerName: the name of the player.
     * @return a list containing the specified player.
     */
    public static List<ProxiedPlayer> getPlayerFromName(String playerName) {
        playerName = playerName.toLowerCase().startsWith("player=") ? playerName.substring(7) : playerName;
        return new ArrayList<>(Collections.singleton(ProxyServer.getInstance().getPlayer(playerName)));
    }

    /**
     * @param serverName: the name of the server.
     * @return a list containing all the players in the server.
     */
    public static List<ProxiedPlayer> getPlayersFromServer(String serverName) {
        serverName = serverName.toLowerCase().startsWith("server=") ? serverName.substring(7) : serverName;
        ProxyServer proxy = ProxyServer.getInstance();
        ServerInfo server = proxy.getServerInfo(serverName);
        if (server == null) return null;
        return getPlayersFromServer(server);
    }

    /**
     * @param server: the server info.
     * @return a list containing all the players in the server.
     */
    public static List<ProxiedPlayer> getPlayersFromServer(ServerInfo server) {
        return new ArrayList<>(server.getPlayers());
    }

    /**
     * @param permission: the permission name.
     * @return a list containing all the players who have the permission.
     */
    public static List<ProxiedPlayer> getPlayersFromPermission(String permission) {
        permission = permission.toLowerCase().startsWith("perm=") ? permission.substring(5) : permission;
        List<ProxiedPlayer> players = new ArrayList<>();
        for (ProxiedPlayer p : getAllPlayers()) if (p.hasPermission(permission)) players.add(p);
        return players;
    }

    /**
     * This will only work if Luck Perms is enabled.
     *
     * @param groupName: the name of the group.
     * @return a list containing all the players in that group.
     */
    public static List<ProxiedPlayer> getPlayersFromGroup(String groupName) {
        List<ProxiedPlayer> players = new ArrayList<>();
        if (!StrippedBroadcastBungee.isLuckPermsEnabled()) return null;
        groupName = groupName.toLowerCase().startsWith("group=") ? groupName.substring(6) : groupName;
        for (ProxiedPlayer p : getAllPlayers()) if (p.hasPermission("group." + groupName)) players.add(p);
        return players;
    }
}