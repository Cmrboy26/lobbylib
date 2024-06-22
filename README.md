### LobbyLib for Bukkit is a simple API for ensuring that minigames do not interfere with each other.

When designing a minigames server, I encountered an issue where a player could be in two separate minigames simultaneously. The lack of communication between each plugin resulted in each plugin "fighting" for control over the player's inventory, position, ability to interact with the world, etc. To fix this, I created this simple API. Each minigame plugin can implement one class, override the join and kick methods, and be completely compatible with each other. In addition to ensuring that the plugins do not "fight," the plugin includes handy leave/join commands alongside a minigame selector compass to allow players to leave/join compatible minigames easily.

## Adding LobbyLib to your server

If you're looking for the JAR file for an existing plugin, refer to the releases tab on the right of your GitHub.
To add it to your server, download the .jar file in the releases tab and put it into the plugins directory of your server.
Once you've restarted the server, any LobbyLib compatible plugins will automatically add themselves to the optional minigame selector compass and
properly function with this plugin's /leave and /join commands. 

If you do not want the minigame selector compass, go into the config of the plugin under ```/plugins/LobbyLib/config.yml``` and set
```use-lobby-selector``` to false.

## Creating a plugin with LobbyLib

First off, you must include the LobbyLib dependency in your plugin's POM file. To do this, go to your POM file and insert the following repositories and dependencies:

```
<repositories>
    ...
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
    ...
</repositories>

<dependencies>
    ...
    <dependency>
        <groupId>com.github.Cmrboy26</groupId>
        <artifactId>lobbylib</artifactId>
        <version>0.0.4</version>
    </dependency>
    <dependency>
        <groupId>com.github.Cmrboy26</groupId>
        <artifactId>lobbylib</artifactId>
        <version>0.0.4</version>
        <classifier>source</classifier>
    </dependency>
    ...
</dependencies>
```

Now that you have LobbyLib as a dependency, go to your Bukkit plugin and implement the ```MinigamePlugin``` interface from ```net.cmr.lobbylib.MinigamePlugin``` and
override the abstract methods as needed.

```java
...
import net.cmr.lobbylib.MinigamePlugin;

public class MyMinigame extends JavaPlugin implements MinigamePlugin {
    ...
    @Override public boolean isPlayerInMinigame(Player player) { ... }
    @Override public void kickPlayerFromMinigame(Player player) { ... }
    @Override public String getMinigameName() { ... }
    ...
}

```
From here, you can fill your code into the overridden methods to make it fit with your minigame!
That's all you need for simple functionality with LobbyLib! If you want to make your plugin compatible with the
optional minigame selector compass that LobbyLib provides for you, you have to implement ```LobbyJoinableMinigame``` instead of ```MinigamePlugin```. The source comments of the method provide the information needed to properly implement the ```LobbyJoinableMinigame``` interface. 




