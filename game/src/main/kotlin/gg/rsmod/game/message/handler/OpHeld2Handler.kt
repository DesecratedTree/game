package gg.rsmod.game.message.handler

import gg.rsmod.game.action.EquipAction
import gg.rsmod.game.message.MessageHandler
import gg.rsmod.game.message.impl.OpHeld2Message
import gg.rsmod.game.model.World
import gg.rsmod.game.model.attr.*
import gg.rsmod.game.model.entity.Client
import java.lang.ref.WeakReference

/**
 * @author Tom <rspsmods@gmail.com>
 */
class OpHeld2Handler : MessageHandler<OpHeld2Message> {
    override fun handle(
        client: Client,
        world: World,
        message: OpHeld2Message,
    ) {
        @Suppress("unused")
        val interfaceId = message.componentHash shr 16

        @Suppress("unused")
        val component = message.componentHash and 0xFFFF

        if (message.slot < 0 || message.slot >= client.inventory.capacity) {
            return
        }

        if (!client.lock.canItemInteract()) {
            return
        }

        if (interfaceId == 763) {
            client.attr[INTERACTING_OPT_ATTR] = 2
            client.attr[INTERACTING_ITEM_ID] = message.item
            client.attr[INTERACTING_SLOT_ATTR] = message.slot
            if (world.plugins.executeButton(client, interfaceId, component)) {
                return
            }
        }

        val item = client.inventory[message.slot] ?: return

        if (item.id != message.item) {
            return
        }

        log(
            client,
            "Item action 2: id=%d, slot=%d, component=(%d, %d), inventory=(%d, %d)",
            message.item,
            message.slot,
            interfaceId,
            component,
            item.id,
            item.amount,
        )

        client.attr[INTERACTING_ITEM] = WeakReference(item)
        client.attr[INTERACTING_ITEM_ID] = item.id
        client.attr[INTERACTING_ITEM_SLOT] = message.slot

        val result = EquipAction.equip(client, item, message.slot)
        if (result == EquipAction.Result.UNHANDLED && world.devContext.debugItemActions) {
            client.writeMessage("Unhandled item action: [item=${item.id}, slot=${message.slot}, option=2]")
        }
    }
}
