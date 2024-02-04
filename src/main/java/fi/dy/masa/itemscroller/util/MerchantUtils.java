package fi.dy.masa.itemscroller.util;

import io.netty.buffer.Unpooled;

import net.minecraft.client.gui.GuiMerchant;
import net.minecraft.inventory.ContainerMerchant;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraft.village.MerchantRecipeList;

import malilib.util.game.wrap.GameWrap;

public class MerchantUtils
{
    public static void changeTradePage(GuiMerchant gui, int page)
    {
        MerchantRecipeList trades = gui.getMerchant().getRecipes(GameWrap.getClientPlayer());

        // The trade list is unfortunately synced after the GUI
        // opens, so the trade list can be null here when we want to
        // restore the last viewed page when the GUI first opens
        if (page >= 0 && (trades == null || page < trades.size()))
        {
            ((IGuiMerchant) gui).setSelectedMerchantRecipe(page);
        }

        ((ContainerMerchant) gui.inventorySlots).setCurrentRecipeIndex(page);
        PacketBuffer packetbuffer = new PacketBuffer(Unpooled.buffer());
        packetbuffer.writeInt(page);
        GameWrap.getClient().getConnection().sendPacket(new CPacketCustomPayload("MC|TrSel", packetbuffer));
    }
}
