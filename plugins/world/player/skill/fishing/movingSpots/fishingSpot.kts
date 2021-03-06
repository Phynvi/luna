package world.player.skill.fishing.movingSpots

import api.predef.*
import io.luna.game.event.impl.ServerLaunchEvent
import io.luna.game.model.Position
import io.luna.game.model.mob.Npc

/**
 * A model representing a fishing spot.
 */
class FishingSpot(id: Int,
                  val home: Position,
                  val away: Position) : Npc(ctx, id, home) {

    companion object {

        /**
         * A range of how often fishing spots can move, in minutes.
         */
        val MOVE_INTERVAL = 1..7 // 1-7 minutes.
    }

    /**
     * The countdown timer. This spot will move when it reaches 0.
     */
    var countdown = MOVE_INTERVAL.random()

    /**
     * Performs a countdown and returns true if the spot should be moved.
     */
    fun countdown(): Boolean {
        countdown--
        if (countdown <= 0) {
            countdown = MOVE_INTERVAL.random()
            return true
        }
        return false
    }
}

/**
 * A list of fishing spots, where all moving fishing spots should be spawned.
 */
val fishingSpots: List<FishingSpot> = listOf()

/**
 * Attempts to move fishing spots from their 'home' to 'away' positions, and vice-versa.
 */
fun moveSpots() {
    fishingSpots.stream()
        .filter { it.countdown() }
        .forEach {
            when (it.position) {
                it.home -> it.teleport(it.away)
                it.away -> it.teleport(it.home)
            }
        }
}

/**
 * Spawns fishing spots.
 */
fun addSpots() = fishingSpots.forEach { world.npcs.add(it) }

// Schedules a task that spawns fishing spots, and attempts to move them every minute.
on(ServerLaunchEvent::class) {
    if (fishingSpots.isNotEmpty()) {
        addSpots()

        world.schedule(100) {
            moveSpots()
        }
    }
}

