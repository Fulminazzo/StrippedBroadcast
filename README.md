# StrippedBroadcast

StrippedBroadcast is a recreation of the Plugin RawMsg (https://www.spigotmc.org/resources/rawmsg.35864/).
The main purpose of the Plugin is to send a message in chat just like the command broadcast. However, when sent, the message will not dispose of a prefix and the sender can specify a group of players to send the message to. (Explained in the Command Section.)
It also has an API available for any developer to be used.

## Commands

The main and only command of StrippedBroadcast is, you guessed it, /sbc (short for /simplebroadcast). Its syntax is: <br />
- /simplebroadcast &lt;targets&gt; &lt;message&gt;

As explained by the Plugin itself when trying to execute the command, the first argument should represent the targets of the broadcast. These could be:
- all, for every player online in the server
- a player, the only user who will see the message
- a world, for every player present in that world
- a permission ("perm=&lt;permission&gt;"), for any player who has the permission

## Permissions

The only permission for StrippedBroadcast is "sbc.main", which will allow the user to execute the /sbc command.

## Colors

Since the Minecraft 1.16 update, Hex Colors were added to the game. Now, StrippedBroadcast is able not only to use the default Minecraft colors ("&"), but also custom ones using the character #. If you are on 1.16 or higher, when typing "#", the plugin will suggest all the valid letters and numbers to be used as color codes. Also, there is a new rainbow effect available. To use it, just type [RAINBOW] in front of your message.
Example:
```
sbc all [RAINBOW] StrippedBroadcast v1.2 is an amazing plugin!
```
![Example](https://github.com/Fulminazzo/StrippedBroadcast/blob/master/example.png)

## API

I honestly have no idea of how this Plugin API could be implemented, but I'm very curious to see what the developers will be able to create. Anyway, after importing the Plugin into your project, you are ready to begin. Three classes will be of your interest:

### StrippedBroadcast

The StrippedBroadcast class contains the static method `sendStrippedBroadcast`. This will send the given message (it can be a string, a list of strings or even an array) to the list of players specified. An example is:

```java
String[] strings = new String[]{"This", "is", "a", "cool", "message"};
StrippedBroadcastEvent.sendStrippedBroadcast(Bukkit.getOnlinePlayers(), strings);
```

### StrippedBroadcastEvent

The StrippedBroadcastEvent is an event called anytime a message is sent using the Plugin Command or the Methods. It contains two variables: a list of involved players and the message sent.

```java
@EventHandler
public void onMessageSent(StrippedBroadcastEvent event) {
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
