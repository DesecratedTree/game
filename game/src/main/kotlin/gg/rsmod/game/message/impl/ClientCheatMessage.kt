package gg.rsmod.game.message.impl

import gg.rsmod.game.message.Message

/**
 * @author Tom <rspsmods@gmail.com>
 */
data class ClientCheatMessage(
    val additionalInformation: Short,
    val command: String,
) : Message
