package net.botwithus;

import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.api.game.hud.inventories.Bank;
import net.botwithus.internal.scripts.ScriptDefinition;
import net.botwithus.rs3.events.impl.InventoryUpdateEvent;
import net.botwithus.rs3.game.*;
import net.botwithus.rs3.game.actionbar.ActionBar;
import net.botwithus.rs3.game.hud.interfaces.Interfaces;
import net.botwithus.rs3.game.movement.Movement;
import net.botwithus.rs3.game.movement.NavPath;
import net.botwithus.rs3.game.movement.TraverseEvent;
import net.botwithus.rs3.game.queries.builders.characters.NpcQuery;
import net.botwithus.rs3.game.queries.builders.items.InventoryItemQuery;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.queries.results.EntityResultSet;
import net.botwithus.rs3.game.queries.results.ResultSet;
import net.botwithus.rs3.game.scene.entities.characters.npc.Npc;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.game.vars.VarManager;
import net.botwithus.rs3.imgui.ImGui;
import net.botwithus.rs3.imgui.NativeInteger;
import net.botwithus.rs3.script.Execution;
import net.botwithus.rs3.script.LoopingScript;
import net.botwithus.rs3.script.Script;
import net.botwithus.rs3.script.config.ScriptConfig;
import net.botwithus.rs3.util.Regex;
import net.botwithus.rs3.game.*;


import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class RunecraftingScript extends LoopingScript {

    private BotState botState = BotState.IDLE;
    private boolean someBool = true;
    private boolean wildernesssword = true;

    private Random random = new Random();
    private Pattern essence = Regex.getPatternForContainingOneOf("Pure essence", "essence");
    private Pattern NumberofRunes = Regex.getPatternForContainingOneOf("rune");
    private Pattern enterance = Regex.getPatternForContainingOneOf("Tendrils", "Rock","Gap","Eyes");
    private Pattern action = Regex.getPatternForContainingOneOf("Squeeze-through","Distract","Mine","Chop");
    //public long scriptStartTime;
    public long scriptStartTime = System.currentTimeMillis();
    //NativeInteger currentItem = new NativeInteger(0);
    String[] options = {"Nature","Fire","Water","Air", "Blood", "Death, Astral"};
    private int currentItem = 0;
    public int numberofrunecrated  = 0;
    public int maigcalthreadcount =0;
    public int runeperhour = 0;
    private Area edgevilleBank = new Area.Rectangular(new Coordinate(3089,3503,0), new Coordinate(3085,3495,0));
    private Area Mage = new Area.Rectangular(new Coordinate(3102,3554,0), new Coordinate(3097,3543,0));
    private Area Abyss = new Area.Rectangular(new Coordinate(3041,4842,0), new Coordinate(3038,4845,0));

    // public int getStartingRunecraftingLevel()
    //{
    //    return startingRunecraftingLevel();
    //}

    public void Runes()
    {
        NativeInteger currentItemNative = new NativeInteger(currentItem);
        if (ImGui.Combo("Rune", currentItemNative,  options))
        {
            currentItem = currentItemNative.get();
            println("Rune Selected" + options[currentItem]);
        }
    }


    private Area TaverleyArea = new Area.Rectangular(new Coordinate(2917,3427,0), new Coordinate(2925,3433,0));

    enum BotState {
        //define your own states here
        IDLE,
        SKILLING,
        BANKING,
        //...

    }

    public RunecraftingScript(String s, ScriptConfig scriptConfig, ScriptDefinition scriptDefinition) {
        super(s, scriptConfig, scriptDefinition);




        //updatestatis();
    }

    @Override
    public boolean initialize()
    {

        this.sgc = new RunecraftingScriptGraphicsContext(getConsole(), this);
        setActive(false);
        runecrafted();

        return super.initialize();

    }


    @Override
    public void onLoop() {
        //Loops every 100ms by default, to change:
        //this.loopDelay = 500;
        LocalPlayer player = Client.getLocalPlayer();
        if (player == null || Client.getGameState() != Client.GameState.LOGGED_IN || botState == BotState.IDLE) {
            //wait some time so we dont immediately start on login.
            Execution.delay(random.nextLong(3000,7000));
            return;
        }
        switch (botState) {
            case IDLE -> {
                //do nothing
                setActive(false);
                println("We're idle!");
                Execution.delay(random.nextLong(1000,3000));
            }
            case SKILLING -> {
               Execution.delay(handleSkilling(player));

            }
            case BANKING -> {
                //handle your banking logic, etc\
                Execution.delay(handleBanking(player));
            }
        }
    }

    private long regionIDFinder(LocalPlayer player)
    {

        println("Region ID" + player.getCoordinate().getRegionId());
        return random.nextLong(1500,3000);

    }
    public void runecrafted()
    {
        subscribe(InventoryUpdateEvent.class, inventoryUpdateEvent -> {
            Item item = inventoryUpdateEvent.getNewItem();
            //println("New Item in Inventory: " + item);
            if (item != null) {
                if (item.getInventoryType().getId() != 93) {
                    return;
                }
                String runeName = item.getName();
                String maigcalthread = item.getName();
                //println("Rune Name:" + runeName);
                if (runeName != null) {
                    if (runeName.contains( " rune")) {
                        numberofrunecrated = numberofrunecrated + item.getStackSize();

                    }
                }
                if(maigcalthread != null)
                {
                    if(maigcalthread.contains( "Magical thread"))
                    {
                        maigcalthreadcount = maigcalthreadcount + item.getStackSize();
                    }
                }

            }
            long currenttime = (System.currentTimeMillis() - scriptStartTime) /1000;
            runeperhour = (int)(Math.round(3600.0 / currenttime * numberofrunecrated));
            //println(" Runes per Hour" + runeperhour);

        });

    }

    private long handleBanking(LocalPlayer player)
    {
        println("Player moving 1:" +player.isMoving());

        if(player.isMoving())
        {
            return random.nextLong(3000,5000);
        }
        if (Bank.isOpen())
        {
            println("Bank is open");
            Bank.loadLastPreset();
              Bank.loadPreset(2);

            //Bank.loadPreset(2);
            botState = BotState.SKILLING;
            return random.nextLong(1000,3000);
        }else
        {
            ResultSet<Item> playerinventory = InventoryItemQuery.newQuery().results();
            if (!Backpack.contains(essence) && player.getAnimationId() == -1) {
            if (Movement.traverse(NavPath.resolve(edgevilleBank.getRandomWalkableCoordinate())) == TraverseEvent.State.FINISHED)
            {
                println("Reached Edgeville Bank Area");
            } else
            {
                println("Unable to reach EdgeVill Bank Area");
            }

                SceneObject BankChest = SceneObjectQuery.newQuery().name("Counter").option("Bank").results().nearest();
                if (BankChest != null) {
                    // WalkToTaverleyBank(player);
                    println("Interacted bank: " + BankChest.interact("Bank"));
                }



            }
        }

        return random.nextLong(1500,3000);
    }
    private long handleSkilling(LocalPlayer player) {
         if (Interfaces.isOpen(1251))
             return random.nextLong(150,3000);


         if (!Backpack.containsAllOf("Pure essence"))
         {

             println("Going to banking state");
             botState = botState.BANKING;
             //Execution.delayUntil(3000, () -> !Interfaces.isOpen(1251));
             return random.nextLong(1500,3000);
         }
        /*println("Player moving:" +player.isMoving());
        println("Player Animation ID :" +player.getAnimationId());*/
        println("Region ID Before Portal Check" + player.getCoordinate().getRegionId());

        if (!(options[currentItem] == "Astral") && player.getCoordinate().getRegionId() != 12107 && player.getCoordinate().getRegionId() != 9547){

            if(Movement.traverse(NavPath.resolve(Mage.getRandomWalkableCoordinate())) == TraverseEvent.State.FINISHED){
                Npc Wizard = NpcQuery.newQuery().name("Mage of Zamorak").results().nearest();
                if(Wizard != null)
                {
                    println("Mage Zam Detected" + Wizard.interact("Teleport"));
                    Execution.delayUntil( 4000, () -> player.getCoordinate().getRegionId() == 12107);

                }
                else
                {
                    println("Mage Zam Is Missing");

                }


            }
            else{
                println("Fail to reach the area");
            }
            return random.nextLong(1000,3000);
        }

        if(player.getCoordinate().getRegionId() == 12107) {
            SceneObject travesal = SceneObjectQuery.newQuery().name( "Rock","Gap","Eyes","Boil").results().nearest();

            SceneObject Naturerift = SceneObjectQuery.newQuery().name("Nature rift").results().nearest();
            SceneObject Bloodrift = SceneObjectQuery.newQuery().name("Blood rift").results().nearest();
            SceneObject Astralerift = SceneObjectQuery.newQuery().name("Nature rift").results().nearest();
            Npc innermage = NpcQuery.newQuery().name("Dark mage").results().nearest();
            //println("Travel" + travesal);

            if (travesal != null && player.distanceTo(innermage) >= 16) {
                println("Interact" + travesal.interact("Squeeze-through"));
                println("Interact" + travesal.interact("Distract"));
                println("Interact" + travesal.interact("Mine"));
                println("Interact" + travesal.interact("Burn-down"));
                Execution.delayUntil(4000,() ->{
                    return player.distanceTo(innermage) <16;
                });

            } else {
                println("Not able to find to get inside the Abyss");
            }

            //SceneObject Naturerift = SceneObjectQuery.newQuery().name("Nature rift").results().nearest();
            //println("Distance to inner mage" + player.distanceTo(innermage));
            if (Naturerift != null && player.distanceTo(innermage) <16  && options[currentItem] == "Nature") {
                println("Interact with Nature Rift" + Naturerift.interact("Exit-through"));
                Execution.delayUntil(2000, () -> {
                    return player.distanceTo(Naturerift) <3;
                });
            }else if(Bloodrift != null && player.distanceTo(innermage) <16  && options[currentItem] == "Blood")
            {
                println("Interact with Nature Rift" + Bloodrift.interact("Exit-through"));
                Execution.delayUntil(2000, () -> {
                    return player.distanceTo(Naturerift) <3;
                });
            }
            else {
                println("Not able to find rift");
            }

        }

        if(player.getCoordinate().getRegionId() == 9547 && options[currentItem] == "Nature")
        {
            SceneObject naturealtar = SceneObjectQuery.newQuery().name("Nature altar").ids(2486).results().nearest();
            if(naturealtar !=null )
            {
                println("Interact with Nature Runes Altar" + naturealtar.interact("Craft-rune"));
                Execution.delay(2000);
            }
        }else if (player.getCoordinate().getRegionId() == 9804 && options[currentItem] == "Blood")
        {
            SceneObject bloodaltar = SceneObjectQuery.newQuery().name("Blood altar").results().nearest();
            if(bloodaltar !=null) {
                println("Interact with Nature Runes Altar" + bloodaltar.interact("Craft-rune"));
                Execution.delay(2000);
            }
        }

        return random.nextLong(1500,3000);
    }


    public BotState getBotState() {
        return botState;
    }

    public void setBotState(BotState botState) {
        this.botState = botState;
    }

    public boolean isSomeBool() {
        return someBool;
    }

    public boolean isBraclet() {
        return wildernesssword;
    }
    public void setBraclet(boolean Bracelet) {
        this.wildernesssword = Bracelet;
    }


    public void setSomeBool(boolean someBool) {
        this.someBool = someBool;
    }
}