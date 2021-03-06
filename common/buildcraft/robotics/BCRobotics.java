package buildcraft.robotics;

import java.util.function.Consumer;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;

import buildcraft.core.BCCore;
import buildcraft.lib.BCLib;
import buildcraft.lib.BCMessageHandler;
import buildcraft.lib.registry.RegistryHelper;
import buildcraft.lib.registry.TagManager;
import buildcraft.lib.registry.TagManager.EnumTagType;
import buildcraft.lib.registry.TagManager.TagEntry;
import buildcraft.robotics.zone.MessageZoneMapRequest;
import buildcraft.robotics.zone.MessageZoneMapResponse;

//@formatter:off
@Mod(modid = BCRobotics.MODID,
name = "BuildCraft Robotics",
version = BCLib.VERSION,
dependencies = "required-after:buildcraftcore@[" + BCLib.VERSION + "]")
//@formatter:on
public class BCRobotics {
    public static final String MODID = "buildcraftrobotics";

    @Mod.Instance(MODID)
    public static BCRobotics INSTANCE = null;

    @Mod.EventHandler
    public static void preInit(FMLPreInitializationEvent evt) {
        RegistryHelper.useOtherModConfigFor(MODID, BCCore.MODID);

        BCRoboticsItems.preInit();
        BCRoboticsBlocks.preInit();

        NetworkRegistry.INSTANCE.registerGuiHandler(INSTANCE, BCRoboticsProxy.getProxy());

        BCMessageHandler.addMessageType(MessageZoneMapRequest.class, MessageZoneMapRequest.HANDLER, Side.SERVER);
        BCMessageHandler.addMessageType(MessageZoneMapResponse.class, MessageZoneMapResponse.HANDLER, Side.CLIENT);
    }

    @Mod.EventHandler
    public static void init(FMLInitializationEvent evt) {
        BCRoboticsProxy.getProxy().fmlInit();
        BCRoboticsRecipes.init();
    }

    @Mod.EventHandler
    public static void postInit(FMLPostInitializationEvent evt) {

    }

    static {
        startBatch();

        // Items

        // Item Blocks
        registerTag("item.block.zone_planner").reg("zone_planner").locale("zonePlannerBlock").model("zone_planner");

        // Blocks
        registerTag("block.zone_planner").reg("zone_planner").oldReg("zonePlannerBlock").locale("zonePlannerBlock").model("zone_planner");

        // Tiles
        registerTag("tile.zone_planner").reg("zone_planner");

        endBatch(TagManager.prependTags("buildcraftrobotics:", EnumTagType.REGISTRY_NAME, EnumTagType.MODEL_LOCATION).andThen(TagManager.setTab("buildcraft.main")));
    }

    private static TagEntry registerTag(String id) {
        return TagManager.registerTag(id);
    }

    private static void startBatch() {
        TagManager.startBatch();
    }

    private static void endBatch(Consumer<TagEntry> consumer) {
        TagManager.endBatch(consumer);
    }
}
