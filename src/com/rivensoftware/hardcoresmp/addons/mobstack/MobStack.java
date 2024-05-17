package com.rivensoftware.hardcoresmp.addons.mobstack;

import com.rivensoftware.hardcoresmp.HardcoreSMP;
import com.rivensoftware.hardcoresmp.tools.MessageTool;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import java.util.HashSet;
import java.util.Set;

public class MobStack 
{

    private static HardcoreSMP plugin = HardcoreSMP.getInstance();
    private static Set<MobStack> stacks = new HashSet<>();
    private static final int SEARCH_SIZE = 15;

    @Getter @Setter private LivingEntity entity;
    @Getter private int count;

    public MobStack(LivingEntity entity, int count) 
    {
        this.entity = entity;
        this.count = count;

        stacks.add(this);

        update();
    }

    public MobStack(LivingEntity entity) 
    {
        this(entity, 1);
    }

    public void setCount(int count) 
    {
        if (count <= 0) 
        {
            System.out.println("removed");
            stacks.remove(this);
            return;
        }

        if (this.count > count) 
        {
            entity = (LivingEntity) entity.getLocation().getWorld().spawnEntity(entity.getLocation(), entity.getType());
            System.out.println("spawned");
        }

        this.count = count;

        update();
    }

    private void update() 
    {
        if (count == 1) 
        {
            entity.setCustomName("");
            entity.setCustomNameVisible(false);
        } 
        else 
        {
            entity.setCustomName(MessageTool.color("&ex%COUNT%").replace("%COUNT%", count + ""));
            entity.setCustomNameVisible(true);
        }

    }

    public static MobStack getByNearby(LivingEntity entity) 
    {
        for (Entity nearby : entity.getNearbyEntities(SEARCH_SIZE, SEARCH_SIZE, SEARCH_SIZE)) 
        {
            if (nearby instanceof LivingEntity) 
            {
                if (nearby.getEntityId() == entity.getEntityId()) continue;
                if (nearby.getType() != entity.getType()) continue;

                MobStack stack = getByEntity((LivingEntity) nearby);
                if (stack != null && stack.getCount() < plugin.getMainConfig().getInt("MOB_STACKING.MAX_COUNT")) 
                {
                    return stack;
                }
            }
        }

        return null;
    }

    public static void stack() 
    {
        for (World world : Bukkit.getWorlds()) 
        {
            for (Entity entity : world.getEntities()) 
            {
                if (entity instanceof LivingEntity  && MobStack.isValid(entity.getType())) 
                {
                    if (entity.getType() == EntityType.PLAYER) continue;
                    if (getByEntity((LivingEntity) entity) != null) continue;

                    MobStack stack = MobStack.getByNearby((LivingEntity) entity);
                    if (stack != null && stack.getCount() < plugin.getMainConfig().getInt("MOB_STACKING.MAX_COUNT")) 
                    {
                        entity.remove();
                        stack.setCount(stack.getCount() + 1);
                    } 
                    else 
                    {
                        new MobStack((LivingEntity) entity);
                    }
                }
            }
        }

        for (MobStack stack : stacks) 
        {
            if (!stack.getEntity().isValid()) 
            {
                stack.setEntity((LivingEntity) stack.getEntity().getWorld().spawnEntity(stack.getEntity().getLocation(), stack.getEntity().getType()));
                stack.setCount(stack.getCount());
            }

        }

    }

    public static void unStack() 
    {
        for (World world : Bukkit.getWorlds()) 
        {
            for (Entity entity : world.getEntities()) 
            {
                if (entity instanceof LivingEntity && MobStack.isValid(entity.getType())) 
                {
                    MobStack stack = MobStack.getByEntity((LivingEntity) entity);
                    if (stack != null) 
                    {

                        for (int i = 1; i < stack.getCount(); i++) 
                        {
                            stack.getEntity().getWorld().spawnEntity(stack.getEntity().getLocation(), stack.getEntity().getType());
                        }

                        stack.getEntity().remove();

                        MobStack.getStacks().remove(stack);
                    }
                }
            }
        }
    }

    public static boolean isValid(EntityType type) 
    {
        return !plugin.getMainConfig().getString("MOB_STACKING.EXCLUDED_MOBS").contains(type.name());
    }

    public static MobStack getByEntity(LivingEntity entity) 
    {
        for (MobStack stack : stacks) 
        {
            if (stack.getEntity().getEntityId() == entity.getEntityId()) 
            {
                return stack;
            }
        }
        return null;
    }

    public static Set<MobStack> getStacks() 
    {
        return stacks;
    }
}
