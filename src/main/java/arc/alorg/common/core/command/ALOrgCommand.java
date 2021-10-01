package arc.alorg.common.core.command;

import arc.alorg.common.item.ALOrgToolItem;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TranslationTextComponent;

public class ALOrgCommand {
    private static final SimpleCommandExceptionType INVALID = new SimpleCommandExceptionType(new TranslationTextComponent("command.alorg.invalid"));

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        LiteralArgumentBuilder<CommandSource> commandBuilder = Commands.literal("alorg")
                .then(Commands.literal("set")
                        .then(Commands.argument("id", IntegerArgumentType.integer(1))
                                .executes(context -> setID(context, IntegerArgumentType.getInteger(context, "id"))))
                );

        LiteralCommandNode<CommandSource> command = dispatcher.register(commandBuilder);
        dispatcher.register(Commands.literal("alo").redirect(command));
    }

    private static int setID(CommandContext<CommandSource> context, int id) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrException();
        ItemStack itemStack = player.getMainHandItem();
        if (!itemStack.isEmpty() && itemStack.getItem() instanceof ALOrgToolItem) {
            ALOrgToolItem.setID(itemStack, id);
            context.getSource().sendSuccess(new TranslationTextComponent("command.alorg.set.success", id), false);
        }

        return Command.SINGLE_SUCCESS;
    }
}
