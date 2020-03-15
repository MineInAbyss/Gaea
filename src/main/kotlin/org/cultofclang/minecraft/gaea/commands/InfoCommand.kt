package org.cultofclang.minecraft.gaea.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Entity
import org.cultofclang.minecraft.gaea.Zone
import org.cultofclang.minecraft.gaea.decay

object InfoCommand : CommandExecutor{
    /**
     * Executes the given command, returning its success.
     * <br></br>
     * If false is returned, then the "usage" plugin.yml entry for this command
     * (if defined) will be sent to the player.
     *
     * @param sender Source of the command
     * @param command Command which was executed
     * @param label Alias of the command which was used
     * @param args Passed command arguments
     * @return true if a valid command, otherwise false
     */
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {


        val bagOfFlags = args.toSet()

        if(sender !is Entity) {
            sender.sendMessage("need location info")
            return false
        }

        val location = sender.location

        val zone = Zone.get(location)?: return false

        sender.sendMessage("The zone ${zone.x}, ${zone.y}, ${zone.z} has ${zone.balance} and is ${zone.timePassed}s")

        if(args.size == 2 && args[0] == "set"){
            zone.update(args[1].toFloat())
        }

        if("regen" in bagOfFlags) {
            decay(zone, true)
        }

        return  true
    }

}