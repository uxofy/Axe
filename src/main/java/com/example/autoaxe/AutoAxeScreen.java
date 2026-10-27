package com.example.autoaxe;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class AutoAxeScreen extends Screen {
    private ButtonWidget enabledBtn, debugBtn, hitsBtn;

    public AutoAxeScreen() {
        super(Text.literal("AutoAxe"));
    }

    @Override
    protected void init() {
        int w = 200, h = 20, x = this.width / 2 - w / 2, y = this.height / 2 - 40;

        enabledBtn = addDrawableChild(ButtonWidget.builder(Text.empty(), b -> {
            AutoAxeConfig.enabled = !AutoAxeConfig.enabled;
            refresh();
        }).dimensions(x, y, w, h).build());

        hitsBtn = addDrawableChild(ButtonWidget.builder(Text.empty(), b -> {
            AutoAxeConfig.extraHits = AutoAxeConfig.extraHits % 3 + 1;
            refresh();
        }).dimensions(x, y + 26, w, h).build());

        debugBtn = addDrawableChild(ButtonWidget.builder(Text.empty(), b -> {
            AutoAxeConfig.debug = !AutoAxeConfig.debug;
            refresh();
        }).dimensions(x, y + 52, w, h).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("Готово"), b -> close())
                .dimensions(x, y + 84, w, h).build());

        refresh();
    }

    private void refresh() {
        enabledBtn.setMessage(Text.literal("Мод: " + (AutoAxeConfig.enabled ? "ВКЛ" : "ВЫКЛ")));
        hitsBtn.setMessage(Text.literal("Лишних ударов: " + AutoAxeConfig.extraHits));
        debugBtn.setMessage(Text.literal("Отладка: " + (AutoAxeConfig.debug ? "ВКЛ" : "ВЫКЛ")));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, this.height / 2 - 65, 0xFFFFFFFF);
    }

    @Override
    public void removed() {
        AutoAxeConfig.save();
    }
}
