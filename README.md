# IceAnarchyCore - Bukkit & NMS Plugin
IceAnarchyCore is a Bukkit & NMS plugin specifically designed for anarchy servers such as iceanarchy.org on Minecraft version 1.12.2. This plugin uses high level programming techniques and has been developed to be as memory safe, efficient, and low level as possible.

The project was started by SevJ6 (me) after a long break from running anarchy servers. The goal was to create a plugin that would enhance the player experience while maintaining server stability and performance. IceAnarchyCore has been designed to work seamlessly and aims to provide a range of features to enhance the anarchy experience.

## Features

## Chat Features
```
Ignore players
Message and reply
Chat toggling
Tab completion filter
include '>' for green chat
```

## Commands
```
playtime | Shows a player's total time spent playing on the server
help | Gives details on available commands to players
joindate | Shows the exact date and time that a player first joined
stats | shows server file size, unique player joins, and total server age
whisper | message a player privately
reply | reply to a private message from a player
verify | verify the integrity of a minecraft username (i.e. check if an account is cracked or not)
distance | return the distance in blocks that you are away from somewhere and how long it would take to walk and fly there
alias | shows all different command aliases for a specified server command
ignore | stop seeing messages and recieving private messages from a player
ignorelist | shows all the players you have ignored currently
unignore | self explantory
```

## Patches
```
ProjectileCrash | stop entities (mostly throwables and projectiles) from entering unloaded chunks
Boatfly | prevent players from using an exploit to fly around with boats
RedstoneLimiter | prevents most known redstone-primary lag/crash machines
PortalLag | prevent too many entities from going through portals (i.e. too many xp orbs, items, ect.)
Godmode | stop players from desyncing their position on the server while riding entities (causing "godmode")
DispenserCrash | prevent dispensing block entities at y levels 0 and 256 (cant believe paper never patched this)
CrystalSlowdown | rate limit how fast you can attack end crystals
BlockPhysics | reduce risk of block updates / physics events from lagging the server
LeverSpam | prevent people from spamming levers (multiple exploits are caused from this, i.e. the redstone dust lag exploit)
EntityCollisions | stop minecarts from lagging the server
PVPExploits | prevent a multitude of various exploits involving 32ks. Stop people from freeroaming around with 32ks, 32k tp, and 32k elytra fly
PacketRateLimiter | implement a packet rate limit of 300 packets per second as the maximum
NoCommentExploit | prevent the coordinate exploit popularized by FitMC, originally discovered by nerdsinc
PacketFly | prevent players from phasing through blocks, and flying using various packetfly modules in their clients
TacoHack | age old exploit discovered by 254n_m involving clicking invalid inventory slots using a client module. Nicknamed "TacoHack"
EntityLimit | limit how many entities can exist per chunk, thus reducing overall lag and improving server performance
LightLag | stop too many light updates from lagging the server
```

## Miscellaneous
```
Auto Server Restart | restart the server after 24 hours of total uptime
Tablist | customize your server's tablist via config.yml
Item Reverts | revert items with a sharpness value of 32767 to normal sharpness 5
First Join Listener | send the player with your server's disord link the first time they join the server
```

## Api Calls
### IceAnarchyCore uses a library called RtMixin to allow modification to the minecraft server source code at runtime.
### [You can check out RtMixin here](https://github.com/254nm/RtMixin)

## How to compile

```bash
./gradlew shadowJar
```
