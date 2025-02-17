/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package KataLightSaberLeague

import io.kotlintest.shouldThrow
import io.kotlintest.specs.StringSpec
import kotlin.test.assertNull

class AppTest : StringSpec({
    "get 3 best players throw no best player found" {
        shouldThrow<NoBestPlayersFoundException> {
            val clan = Clan("Helico", listOf())

            clan.get3BestPlayers()
        }
    }

    "get 3 best players return minimum 1 player" {
        val clan = Clan("Helico", listOf(Player("Robin", 0)))

        assert(clan.get3BestPlayers().isNotEmpty())
    }

    "get 3 best players return maximum 3 players" {
        val clan = Clan(
            "Helico", listOf(
                Player("Robin", 0), Player("Erika", 0),
                Player("Christelle", 0), Player("Robin n2", 0)
            )
        )

        assert(clan.get3BestPlayers().size < 4)
    }

    "get 3 best players return 2 differents players" {
        val player = Player("Robin", 0)
        val players = listOf(
            player, player,
            player, Player("Robin", 0)
        )
        val clan = Clan(
            "Helico", players
        )

        assert(clan.get3BestPlayers().size == 2)
    }

    "winner should be above looser after a fight when winner is below looser" {
        val winner = Player("Erika", 0)
        val looser = Player("Robin", 0)
        val clan = Clan(
            "Helico", listOf(
                looser, Player("Christelle", 0),
                winner, Player("Le Fantome", 0)
            )
        )
        val battle = InnerClanBattle()
        val winnerPlayer = battle.fight(winner, looser)
        clan.registerFightScore(winnerPlayer!!, looser)
        assert(clan.get3BestPlayers()[0] == winner)
        assert(clan.get3BestPlayers()[1] == looser)
    }

    "if winner is above looser then it should do nothing" {
        val winner = Player("Erika", 0)
        val looser = Player("Robin", 0)
        val clan = Clan(
            "versateam", listOf(
                winner, Player("Didi", 100),
                looser, Player("Mon Seigneur", 0)
            )
        )
        val battle = InnerClanBattle()
        val winnerPlayer = battle.fight(winner, looser)
        clan.registerFightScore(winnerPlayer!!, looser)
        assert(clan.get3BestPlayers()[0] == winner)
        assert(clan.get3BestPlayers()[2] == looser)
    }

    "if player is fighting himself then should do nothing" {
        val player = Player("Erika", 0)
        val clanErika = Clan("erika", listOf(player))
        val battle = InnerClanBattle()
        val noWinner = battle.fight(player, player)

        clanErika.registerFightScore(noWinner, noWinner)

        assertNull(noWinner)
        assert(clanErika.get3BestPlayers()[0] == player)
    }
})

class NoBestPlayersFoundException : Exception()

class Player(val name: String, var experience: Int)

class Clan(val name: String, players: List<Player>) {

    private val players: MutableList<Player> = players.distinct().toMutableList()

    @Throws(NoBestPlayersFoundException::class)
    fun get3BestPlayers(): List<Player> {
        if (players.isEmpty()) {
            throw NoBestPlayersFoundException()
        }

        return if (players.size > 3) {
            players.subList(0, 3)
        } else {
            players
        }
    }

    fun registerFightScore(winner: Player?, looser: Player?) {
        if (winner == null || looser == null)
            return

        if (players.indexOf(winner) < players.indexOf(looser))
            return

        players.remove(winner)
        val indexOfLooser = players.indexOf(looser)
        players.add(indexOfLooser, winner)
    }
}

interface Battle {
    fun fight(player1: Player, player2: Player): Player?
}

class InnerClanBattle : Battle {
    override fun fight(player1: Player, player2: Player): Player? {
        if (player1 == player2) return null
        return player1
    }
}
