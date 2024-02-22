package net.botwithus;

import net.botwithus.rs3.events.impl.SkillUpdateEvent;
import net.botwithus.rs3.game.Client;
import net.botwithus.rs3.game.skills.Skill;
import net.botwithus.rs3.imgui.ImGui;
import net.botwithus.rs3.imgui.ImGuiWindowFlag;
import net.botwithus.rs3.script.ScriptConsole;
import net.botwithus.rs3.script.ScriptGraphicsContext;
import net.botwithus.rs3.game.skills.Skills;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;

public class CraftingScriptGraphicsContext extends ScriptGraphicsContext {

    private CraftingScript script;

    public CraftingScriptGraphicsContext(ScriptConsole scriptConsole, CraftingScript script) {
        super(scriptConsole);
        this.script = script;
    }


    @Override
    public void drawSettings() {
        if (ImGui.Begin("Gem Mining", ImGuiWindowFlag.None.getValue())) {
            if (ImGui.BeginTabBar("Bar", ImGuiWindowFlag.None.getValue())) {
                if (ImGui.BeginTabItem("Play", ImGuiWindowFlag.None.getValue())) {
                    ImGui.Text("Welcome to Al Kharid Mining Script");
                    ImGui.Text("Scripts state is: " + script.getBotState());
                    if (ImGui.Button("Start")) {
                        //button has been clicked
                        script.setBotState(CraftingScript.BotState.SKILLING);
                    }
                    ImGui.SameLine();
                    if (ImGui.Button("Stop")) {
                        //has been clicked
                        script.setBotState(CraftingScript.BotState.IDLE);
                    }
                    ImGui.EndTabItem();
                }
                if (ImGui.BeginTabItem("Stats", ImGuiWindowFlag.None.getValue())) {
                    ImGui.Text("Mining");
                    ImGui.Text("Mining Level: " + Skills.MINING.getLevel());
                    ImGui.Text("Remaining XP to Next level: " + Skills.MINING.getExperienceToNextLevel());

                    ImGui.EndTabItem();
                }
                if (ImGui.BeginTabItem("Config", ImGuiWindowFlag.None.getValue())) {
                    ImGui.Text("Please configure unlock Available:");
                    script.setSomeBool(ImGui.Checkbox("War's Retreat Teleport", script.isSomeBool()));
                    ImGui.EndTabItem();
                }
                ImGui.EndTabBar();
            }
            ImGui.End();
        }

    }

    @Override
    public void drawOverlay() {
        super.drawOverlay();
    }
}

