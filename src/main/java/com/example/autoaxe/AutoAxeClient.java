package com.example.autoaxe;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;

public class AutoAxeClient implements ClientModInitializer {
    private static boolean busy = false;

    private static void dbg(MinecraftClient c, String s) {
        if (AutoAxeConfig.debug && c.player != null) c.player.sendMessage(Text.literal("[AutoAxe] " + s), true);
    }

    @Override
    public void onInitializeClient() {
        AutoAxeConfig.load();

        // /autoaxe - открыть меню настроек
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
            dispatcher.register(ClientCommandManager.literal("autoaxe").executes(ctx -> {
                MinecraftClient mc = MinecraftClient.getInstance();
                // открываем на следующем тике, иначе чат закроет экран
                mc.send(() -> mc.setScreen(new AutoAxeScreen()));
                return 1;
            })));

        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (!AutoAxeConfig.enabled || !world.isClient() || busy) return ActionResult.PASS;

            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null || client.interactionManager == null) return ActionResult.PASS;
            if (player != client.player) return ActionResult.PASS;

            if (!player.getMainHandStack().isIn(ItemTags.AXES)) { dbg(client, "в руке не топор"); return ActionResult.PASS; }
            if (!(entity instanceof PlayerEntity target)) { dbg(client, "цель не игрок"); return ActionResult.PASS; }

            boolean blocking = target.isBlocking()
                    || (target.isUsingItem() && target.getActiveItem().isOf(Items.SHIELD));
            if (!blocking) { dbg(client, "щит не поднят"); return ActionResult.PASS; }

            busy = true;
            try {
                for (int i = 0; i < AutoAxeConfig.extraHits; i++) {
                    client.interactionManager.attackEntity(player, target);
                }
                dbg(client, "двойной удар!");
            } finally {
                busy = false;
            }
            return ActionResult.PASS;
        });
    }
}
