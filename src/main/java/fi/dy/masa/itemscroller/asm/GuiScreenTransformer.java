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

public class GuiScreenTransformer implements IClassTransformer
{
    private static final String[] CLASSES_TO_TRANSFORM = new String[] {
            "net.minecraft.client.gui.GuiScreen"
    };

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytesIn)
    {
        final int index = Arrays.asList(CLASSES_TO_TRANSFORM).indexOf(transformedName);
        return index != -1 ? this.transform_GuiScreen(bytesIn) : bytesIn;
    }

    private byte[] transform_GuiScreen(byte[] bytesIn)
    {
        try
        {
            ClassNode classNode = new ClassNode();
            ClassReader classReader = new ClassReader(bytesIn);
            classReader.accept(classNode, 0);

            ItemScrollerLoadingPlugin.LOGGER.info("GuiScreenTransformer: Trying to hook the GuiScreen methods handleMouseInput() and handleKeyboardInput()");
            ItemScrollerLoadingPlugin.LOGGER.info("Obfuscated: {}", ItemScrollerLoadingPlugin.isObfuscated);

            this.transformMethod_handleMouseInput(classNode, ItemScrollerLoadingPlugin.isObfuscated);
            this.transformMethod_handleKeyboardInput(classNode, ItemScrollerLoadingPlugin.isObfuscated);

            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            classNode.accept(writer);
            byte[] bytes = writer.toByteArray();

            ItemScrollerLoadingPlugin.LOGGER.info("GuiScreenTransformer: Finished hooking the GuiScreen#handleInput() method");

            return bytes;
        }
        catch (Exception e)
        {
            ItemScrollerLoadingPlugin.LOGGER.error("Failed to patch GuiScreen", e);
        }

        return bytesIn;
    }

    private void transformMethod_handleMouseInput(ClassNode classNode, boolean obfuscated)
    {
        final String targetMethodName = obfuscated ? "k" : "handleMouseInput"; // func_146274_d
        final String targetMethodDesc = "()V";
        final String paramType = obfuscated ? "(Lbdw;)Z" : "(Lnet/minecraft/client/gui/GuiScreen;)Z";

        for (MethodNode method : classNode.methods)
        {
            if (method.name.equals(targetMethodName) && method.desc.equals(targetMethodDesc))
            {
                AbstractInsnNode firstNode = null;
                AbstractInsnNode returnNode = null;

                for (AbstractInsnNode instruction : method.instructions.toArray())
                {
                    if (instruction.getOpcode() == Opcodes.INVOKESTATIC &&
                        ((MethodInsnNode) instruction).name.equals("getEventX") &&
                        firstNode == null)
                    {
                        firstNode = instruction;
                    }
                    else if (instruction.getOpcode() == Opcodes.RETURN)
                    {
                        returnNode = instruction;
                    }

                    if (firstNode != null && returnNode != null)
                    {
                        ItemScrollerLoadingPlugin.LOGGER.info("GuiScreenTransformer: Hooking the handleMouseInput() method");

                        LabelNode labelNode = new LabelNode();
                        InsnList toInsert = new InsnList();

                        toInsert.add(new VarInsnNode(Opcodes.ALOAD, 0));
                        toInsert.add(new MethodInsnNode(
                                Opcodes.INVOKESTATIC,
                                "fi/dy/masa/itemscroller/asm/Hooks",
                                "fireMouseInputEvent",
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

    private void transformMethod_handleKeyboardInput(ClassNode classNode, boolean obfuscated)
    {
        final String targetMethodName = obfuscated ? "l" : "handleKeyboardInput"; // func_146282_l
        final String targetMethodDesc = "()V";
        final String paramType = obfuscated ? "(Lbdw;)Z" : "(Lnet/minecraft/client/gui/GuiScreen;)Z";

        for (MethodNode method : classNode.methods)
        {
            if (method.name.equals(targetMethodName) && method.desc.equals(targetMethodDesc))
            {
                AbstractInsnNode firstNode = null;
                AbstractInsnNode returnNode = null;

                for (AbstractInsnNode instruction : method.instructions.toArray())
                {
                    if (instruction.getOpcode() == Opcodes.INVOKESTATIC &&
                        ((MethodInsnNode) instruction).name.equals("getEventKeyState") &&
                        firstNode == null)
                    {
                        firstNode = instruction;
                    }
                    else if (instruction.getOpcode() == Opcodes.RETURN)
                    {
                        returnNode = instruction;
                    }

                    if (firstNode != null && returnNode != null)
                    {
                        ItemScrollerLoadingPlugin.LOGGER.info("GuiScreenTransformer: Hooking the handleKeyboardInput() method");

                        LabelNode labelNode = new LabelNode();
                        InsnList toInsert = new InsnList();

                        toInsert.add(new VarInsnNode(Opcodes.ALOAD, 0));
                        toInsert.add(new MethodInsnNode(
                                Opcodes.INVOKESTATIC,
                                "fi/dy/masa/itemscroller/asm/Hooks",
                                "fireKeyboardInputEvent",
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
