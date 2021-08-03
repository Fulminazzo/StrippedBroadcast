# StrippedBroadcast

StrippedBroadcast is a recreation of the Plugin RawMsg (https://www.spigotmc.org/resources/rawmsg.35864/).<br />
The main purpouse of the Plugin is to send a message in chat just like the command broadcast. However, when sent, the message will not dispose of a prefix and the sender can specify a group of players to send the message to. (Explained in the Command Section.)
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
<br />

## API

I honestly have no idea of how this Plugin API could be implemented, but I'm very curious to see what the developers will be able to create. Anyway, after importing the Plugin into your project, you are ready to begin. Two classes will be of your interest:

#### StrippedBroadcast

The class StrippedBroadcast contains the static method `sendStrippedBroadcast`. This will send the given message (it can be a string, a list of strings or even an array) to the list of players specified. An example is:

```sh
String[] strings = new String[]{"This", "is", "a", "cool", "message"};
StrippedBroadcastEvent.sendStrippedBroadcast(Bukkit.getOnlinePlayers(), strings);
```

#### StrippedBroadcastEvent

The StrippedBroadcastEvent is an event called anytime a message is sent using the Plugin Command or the Methods. It contains two variables: a list of involved players and the message sent.

```sh
@EventHandler
public void onMessageSent(StrippedBroadcastEvent event) {
    event.getMessage();
}
```
