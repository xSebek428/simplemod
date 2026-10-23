package com.example.simplehud;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public class SimpleHudClient implements ClientModInitializer {

    private static final int COLOR_TEXT = 0xFFFFFFFF;
    private static final int MARGIN_X = 4;
    private static final int MARGIN_Y = 4;
    private static final int LINE_HEIGHT = 10;

    private static final EquipmentSlot[] ARMOR_SLOTS = {
            EquipmentSlot.HEAD,
            EquipmentSlot.CHEST,
            EquipmentSlot.LEGS,
            EquipmentSlot.FEET
    };

    private static final String[] SLOT_LABELS = {
            "Hełm", "Napierśnik", "Spodnie", "Buty"
    };

    @Override
    public void onInitializeClient() {
        HudRenderCallback.EVENT.register(this::renderHud);
    }

    private void renderHud(DrawContext context, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;

        if (player == null || client.options.hudHidden) {
            return;
        }

        TextRenderer textRenderer = client.textRenderer;
        int y = MARGIN_Y;

        drawLine(context, textRenderer, "FPS: " + client.getCurrentFps(), y);
        y += LINE_HEIGHT;

        String coords = String.format("XYZ: %d, %d, %d",
                (int) Math.round(player.getX()),
                (int) Math.round(player.getY()),
                (int) Math.round(player.getZ()));
        drawLine(context, textRenderer, coords, y);
        y += LINE_HEIGHT;

        for (int i = 0; i < ARMOR_SLOTS.length; i++) {
            ItemStack stack = player.getEquippedStack(ARMOR_SLOTS[i]);
            String line;

            if (stack.isEmpty()) {
                line = SLOT_LABELS[i] + ": Brak";
            } else if (stack.isDamageable()) {
                int max = stack.getMaxDamage();
                int remaining = max - stack.getDamage();
                line = stack.getName().getString() + ": " + remaining + "/" + max;
            } else {
                line = stack.getName().getString();
            }

            drawLine(context, textRenderer, line, y);
            y += LINE_HEIGHT;
        }
    }

    private void drawLine(DrawContext context, TextRenderer textRenderer, String text, int y) {
        context.drawTextWithShadow(textRenderer, Text.literal(text), MARGIN_X, y, COLOR_TEXT);
    }
}
