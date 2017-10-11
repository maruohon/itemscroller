package fi.dy.masa.itemscroller.asm;

import java.util.Arrays;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import net.minecraft.launchwrapper.IClassTransformer;

public class ContainerTransformer implements IClassTransformer
{
    private static final String[] CLASSES_TO_TRANSFORM = new String[] {
            "net.minecraft.inventory.Container"
    };

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytesIn)
    {
        final int index = Arrays.asList(CLASSES_TO_TRANSFORM).indexOf(transformedName);
        return index != -1 ? this.transform_Container(bytesIn) : bytesIn;
    }

    private byte[] transform_Container(byte[] bytesIn)
    {
        try
        {
            ClassNode classNode = new ClassNode();
            ClassReader classReader = new ClassReader(bytesIn);
            classReader.accept(classNode, 0);

            ItemScrollerLoadingPlugin.LOGGER.info("ContainerTransformer: Trying to hook the Container#slotChangedCraftingGrid() method");
            ItemScrollerLoadingPlugin.LOGGER.info("ContainerTransformer: Obfuscated = {}", ItemScrollerLoadingPlugin.isObfuscated);

            this.transformMethod_slotChangedCraftingGrid(classNode, ItemScrollerLoadingPlugin.isObfuscated);

            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            classNode.accept(writer);
            byte[] bytes = writer.toByteArray();

            ItemScrollerLoadingPlugin.LOGGER.info("ContainerTransformer: Finished hooking the Container#slotChangedCraftingGrid() method");

            return bytes;
        }
        catch (Exception e)
        {
            ItemScrollerLoadingPlugin.LOGGER.error("ContainerTransformer: Failed to patch GuiScreen", e);
        }

        return bytesIn;
    }

    private void transformMethod_slotChangedCraftingGrid(ClassNode classNode, boolean obfuscated)
    {
        final String targetMethodName = obfuscated ? "a" : "slotChangedCraftingGrid"; // func_192389_a
        final String targetMethodDesc = obfuscated ? "(Lams;Laeb;Lafw;Lagl;)V" : "(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/inventory/InventoryCrafting;Lnet/minecraft/inventory/InventoryCraftResult;)V";
        final String paramType = obfuscated ? "(Lafp;Lams;Lafw;Lagl;)Z" : "(Lnet/minecraft/inventory/Container;Lnet/minecraft/world/World;Lnet/minecraft/inventory/InventoryCrafting;Lnet/minecraft/inventory/InventoryCraftResult;)Z";

        for (MethodNode method : classNode.methods)
        {
            if (method.name.equals(targetMethodName) && method.desc.equals(targetMethodDesc))
            {
                AbstractInsnNode firstNode = null;
                AbstractInsnNode returnNode = null;

                for (AbstractInsnNode instruction : method.instructions.toArray())
                {
                    if (instruction.getOpcode() == Opcodes.ALOAD && firstNode == null)
                    {
                        firstNode = instruction;
                    }
                    else if (instruction.getOpcode() == Opcodes.RETURN)
                    {
                        returnNode = instruction;
                    }

                    if (firstNode != null && returnNode != null)
                    {
                        ItemScrollerLoadingPlugin.LOGGER.info("ContainerTransformer: Hooking the slotChangedCraftingGrid() method");

                        LabelNode labelNode = new LabelNode();
                        InsnList toInsert = new InsnList();

                        toInsert.add(new VarInsnNode(Opcodes.ALOAD, 0)); // this
                        toInsert.add(new VarInsnNode(Opcodes.ALOAD, 1)); // first argument (World)
                        toInsert.add(new VarInsnNode(Opcodes.ALOAD, 3)); // third argument (InventoryCrafting)
                        toInsert.add(new VarInsnNode(Opcodes.ALOAD, 4)); // fourth argument (InventoryCraftResult)
                        toInsert.add(new MethodInsnNode(
                                Opcodes.INVOKESTATIC,
                                "fi/dy/masa/itemscroller/asm/Hooks",
                                "fireCraftingEventSlotChanged",
                                paramType,
                                false));
                        toInsert.add(new JumpInsnNode(Opcodes.IFNE, labelNode));

                        method.instructions.insertBefore(firstNode, toInsert);
                        method.instructions.insertBefore(returnNode, labelNode);

                        return;
                    }
                }
            }
        }
    }
}