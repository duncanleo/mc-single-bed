package me.duncanleo.mc_single_bed

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerBedEnterEvent
import org.bukkit.event.player.PlayerBedLeaveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import java.util.*

class App : JavaPlugin(), Listener {
  private val playerTaskMap = mutableMapOf<UUID, BukkitTask>()

  override fun onEnable() {
    logger.info("Hello there!")

    server.pluginManager.registerEvents(this, this)
  }

  @EventHandler
  fun playerBedEnter(event: PlayerBedEnterEvent) {
    if (event.bedEnterResult != PlayerBedEnterEvent.BedEnterResult.OK) {
      return
    }

    val task = object: BukkitRunnable() {
      override fun run() {
        if (!isMultiplePlayersOnline()) {
          return
        }
        event.player.world.time = 0
        Bukkit.broadcastMessage("${ChatColor.GOLD}Rise and Shine! ${ChatColor.YELLOW}Brought to you by ${ChatColor.AQUA}${event.player.displayName}")
      }
    }.runTaskLater(this, 20 * 10)
    playerTaskMap[event.player.uniqueId] = task
  }

  @EventHandler
  fun playerBedLeave(event: PlayerBedLeaveEvent) {
    val uuid = event.player.uniqueId
    playerTaskMap[uuid]?.cancel()
    playerTaskMap.remove(uuid)
  }

  @EventHandler
  fun playerQuit(event: PlayerQuitEvent) {
    val uuid = event.player.uniqueId
    playerTaskMap[uuid]?.cancel()
    playerTaskMap.remove(uuid)
  }

  private fun isMultiplePlayersOnline(): Boolean {
    return Bukkit.getOnlinePlayers().size > 1
  }
}
