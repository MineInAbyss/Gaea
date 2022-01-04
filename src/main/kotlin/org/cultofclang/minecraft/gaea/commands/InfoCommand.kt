package org.cultofclang.minecraft.gaea.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.cultofclang.minecraft.gaea.Broker
import org.cultofclang.minecraft.gaea.Gaea
import org.cultofclang.minecraft.gaea.Zone
import org.cultofclang.utils.ZONE_SIZE
import org.cultofclang.utils.durationHuman
import kotlin.math.ceil

object InfoCommand : CommandExecutor {
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

        if (sender !is Player) {
            sender.sendMessage("need location info")
            return false
        }


        val location = sender.location

        val zone = Zone.get(location) ?: return false


        if (args.size == 2 && args[0] == "set") {
            zone.set(args[1].toFloat())
        }

        if ("d" in bagOfFlags) {
            zone.decay(false)
        }

        if ("r" in bagOfFlags) {
            zone.decay(true)
        }


        if ("c" in bagOfFlags) {
            val r = Gaea.settings.claimRadius

            val hs = sender.inventory.itemInMainHand
            val handValue = Broker.value(hs)

            if (handValue < Gaea.settings.claimCost) {
                sender.sendMessage("$handValue is not enough to claim chunks")
                return false
            }

            val need = ceil(hs.amount * Gaea.settings.claimCost / handValue).toInt()
            hs.amount -= need

            sender.sendMessage("paid to claim")

            val roi = -r..r step ZONE_SIZE
            for (x in roi)
                for (y in roi)
                    for (z in roi) {
                        Zone.get(location.clone().add(x.toDouble(), y.toDouble(), z.toDouble()))!!
                            .claim(sender, Gaea.settings.claimTime)
                    }

            // do a claim
        }

        sender.sendMessage(
            "The zone ${zone.x}, ${zone.y}, ${zone.z} is ${if (zone.changed) "changed" else "unchanged"} safe for ${
                durationHuman(
                    zone.effectiveBalance
                )
            }"
        )

        return true
    }

}
