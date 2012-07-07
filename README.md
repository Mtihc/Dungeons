Dungeons
========

The Dungeons Minecraft plugin for Bukkit. 

## Description

Dungeons are regions that can be entered using a button. 
When a button is pressed. The region starts restoring. 
When it's finished (and the start-delay is also passed), the dungeon is started. 

All players that pressed the button will join. 
They will be teleported to a random location in the dungeon's spawn region. 
Then, when all the players are inside the exit-region, at the end of the dungeon. 
They will all teleport to the despawn-region, which can be anywhere. 



The dungeon can be started again using the button, when it's not occupied. 
When multiple dungeons are started at the same time, the restoring of the regions will be queued. 
So there will never be two regions that are restored at once. This prevents lag and even server crashes. 

When a player disconnects for some amount of time, he will automatically leave the dungeon. 
Also, when a player dies, he will automatically leave the dungeon. 

The stone buttons that are used to start dungeons are, automatically protected against breaking/explosions/fire. 

## Commands

Type <code>/dungeon</code> to see a list of commands. 

The create command will guide you through the process of creating a dungeon. 