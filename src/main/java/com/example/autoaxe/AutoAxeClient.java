package com.example.autoaxe;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.ActionResult;

public class AutoAxeClient implements ClientModInitializer {
    // Защита от рекурсии: наш дополнительный удар сам вызывает это же событие
    private static boolean busy = false;

    @Override
    public void onInitializeClient() {
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            // Только на клиенте и только на ТВОЙ клик (событие срабатывает при ударе игрока)
            if (!world.isClient() || busy) return ActionResult.PASS;

            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null || client.interactionManager == null) return ActionResult.PASS;
            if (player != client.player) return ActionResult.PASS;

            // Топор в руке, цель - игрок со щитом
            if (!player.getMainHandStack().isIn(ItemTags.AXES)) return ActionResult.PASS;
            if (!(entity instanceof PlayerEntity target) || !target.isBlocking()) return ActionResult.PASS;

            // Один дополнительный удар; второй - твой собственный клик (пройдёт следом)
            busy = true;
            try {
                client.interactionManager.attackEntity(player, target);
            } finally {
                busy = false;
            }
            return ActionResult.PASS;
        });
    }
}
