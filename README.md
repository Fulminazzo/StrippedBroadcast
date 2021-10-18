# StrippedBroadcast

StrippedBroadcast is a recreation of the Plugin RawMsg (https://www.spigotmc.org/resources/rawmsg.35864/).
The main purpose of the Plugin is to send a message in chat just like the command broadcast. However, when sent, the message will not dispose of a prefix and the sender can specify a group of players to send the message to. (Explained in the Command Section.)
It also has an API available for any developer to be used.

## Commands

The main and only command of StrippedBroadcast is, you guessed it, /sbc (short for /simplebroadcast). Its syntax is: <br />
- /strippedbroadcast &lt;targets&gt; &lt;message&gt;

As explained by the Plugin itself when trying to execute the command, the first argument should represent the targets of the broadcast. These could be:
- all, for every player online in the server
- a player, the only user who will see the message
- a world ("world=&lt;world&gt;"), for every player present in that world
- a permission ("perm=&lt;permission&gt;"), for any player who has the permission
- a group ("group=&lt;group&gt;"), for any player who is in that group (REQUIRES LUCKPERMS)
- an item ("item=&lt;item&gt;"), for any player who have that item in their inventory
- an effect ("effect=&lt;effect&gt;"), for any player who have that effect active
- a gamemode ("gamemode=&lt;gamemode&gt;"), for any player who is in that gamemode
- a number ("money=&lt;money&gt;"), for any player that have that quantity in their balance (REQUIRES VAULT)

## Expressions

StrippedBroadcast is also able to understand expressions as target. The user will be able to specify certain conditions that will return a list of compatible players.
Every expression should be expressed in parenthesis, but there is possibility to put more parenthesis inside one.
Example:
```
sbc (world=world_the_nether && (perm=bukkit.* || perm=minecraft.*)) Looks like you are the king of hell![/code]
```

You will also notice that expressions accept the keywords AND (&&) and OR (||). Their functioning is really simple:
- in the above example, the OR keyword is used to get every player who has the permission "bukkit.*" or the permission "minecraft.*";
- the AND keyword is then used to get every player who is in the nether and has one of the two permissions.

Also, the plugin uses Tab Completition, so it will be very helpful at remembering the user to close parenthesis or insert certain keywords.

## Permissions

The only permission for StrippedBroadcast is "sbc.main", which will allow the user to execute the /sbc command.

## Colors

Since the Minecraft 1.16 update, Hex Colors were added to the game. Now, StrippedBroadcast is able not only to use the default Minecraft colors ("&"), but also custom ones using the character #. If you are on 1.16 or higher, when typing "#", the plugin will suggest all the valid letters and numbers to be used as color codes. Also, there is a new rainbow effect available. To use it, just type [RAINBOW] in front of your message.
Example:
```
sbc all [RAINBOW] StrippedBroadcast v1.2 is an amazing plugin!
```
![Example](https://github.com/Fulminazzo/StrippedBroadcast/blob/master/example.png)

## BungeeCord

Recently a version of StrippedBroadcast compatible with BungeeCord was released. It uses the same functionalities as the Spigot one, however some argument do change:
- all, for every player online in the server
- a player, the only user who will see the message
- a server ("server=&lt;server&gt;"), for every player present in that server
- a permission ("perm=&lt;permission&gt;"), for any player who has the permission
- a group ("group=&lt;group&gt;"), for any player who is in that group (REQUIRES LUCKPERMS)

An API is available also for BungeeCord, that uses the same names as the Spigot API followed by a "B" (e.g. PlayerUtilsB is the PlayerUtils for the BungeeCord part of the Plugin). The Main Class is accessible by the name StrippedBroadcastBungee and it contains all of the methods to send a message to a list of players.

## API

I honestly have no idea of how this Plugin API could be implemented, but I'm very curious to see what the developers will be able to create. Anyway, after importing the Plugin into your project, you are ready to begin. Three classes will be of your interest:

### StrippedBroadcast

The StrippedBroadcast class contains the static method `sendStrippedBroadcast`. This will send the given message (it can be a string, a list of strings or even an array) to the list of players specified. An example is:

```java
String[] strings = new String[]{"This", "is", "a", "cool", "message"};
StrippedBroadcastEvent.sendStrippedBroadcast(Bukkit.getOnlinePlayers(), strings);
```

### StrippedBroadcastEvent and StrippedBroadcastEventB

The StrippedBroadcastEvent and StrippedBroadcastEventB are events called anytime a message is sent using the Plugin Command or the Methods. They contain two variables: a list of involved players and the message sent.

```java
@EventHandler
public void onMessageSent(StrippedBroadcastEvent event) {
    event.getMessage();
}
```

```java
@EventHandler
public void onBungeeMessageSent(StrippedBroadcastEventB event) {
    event.getMessage();
}
```

### PlayersUtil

The PlayersUtil class contains all the methods that can be used to get list of players using certain input. Just like the /sbc command, PlayersUtil supports:

`getAllPlayers()` returns a list containing all Online Players.<br>
`getUserPlayer(CommandSender sender)` returns a list containing the sender of the command (if it is console, il will be null).<br>
`getPlayerFromName(String playerName)` returns a list containing the Player with the name "playerName".<br>
`getPlayersFromWorld(String worldName)` returns a list containing all the players in the world "worldName".<br>
`getPlayersFromPermission(String permission)` returns a list containing all the players with the permission "permission".<br>
`getOPPlayers()` returns a list containing all the operator players.<br>
`getPlayersFromGroup(String groupName)` returns a list containing all the players in the LuckPerms group "groupName".<br>
`getPlayersFromEffect(String effectName, String level)` returns a list containing all the players with effect "effectName" of level "level".<br>
`getPlayersFromItem(String itemName, String amount)` returns a list containing all the players that have the item "itemName" of amount "amount" in their inventory.<br>
`getPlayersFromGameMode(String gameModeString)` returns a list containing all the players in gamemode "gameModeString".<br>
`getPlayersFromMoney(String money)` returns a list containing all the players that have "money" Vault balance.<br>

Also, every method is associated with submethods for each parameter. For example:<br>
`getPlayersFromEffect(String effectName, String level)`<br>
can be used as:<br>
`getPlayersFromEffect(String effectName)` (returns a list of all the players with effect "effectName", despite of the level)<br>
`getPlayersFromEffect(String effectName, int level)`<br>
`getPlayersFromEffect(PotionEffectType effect, int level)`<br>
`getPlayersFromEffect(PotionEffectType effect)`<br>

### PlayersUtilB

Just like PlayersUtil, also the BungeeCord part of the plugin has a PlayersUtilB that contains all the methods to access a list of players based on a certain input. This supports:
`getAllPlayers()` returns a list containing all Online Players.<br>
`getUserPlayer(CommandSender sender)` returns a list containing the sender of the command (if it is console, il will be null).<br>
`getPlayerFromName(String playerName)` returns a list containing the Player with the name "playerName".<br>
`getPlayersFromServer(String serverdName)` returns a list containing all the players in the server "serverName".<br>
`getPlayersFromPermission(String permission)` returns a list containing all the players with the permission "permission".<br>
`getPlayersFromGroup(String groupName)` returns a list containing all the players in the LuckPerms group "groupName".<br>
