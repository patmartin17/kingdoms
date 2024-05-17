package com.rivensoftware.hardcoresmp.addons.mobstack;

import com.rivensoftware.hardcoresmp.HardcoreSMP;
import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MobStackListeners implements Listener 
{
	@EventHandler
	public void onCreatureSpawnEvent(CreatureSpawnEvent event) 
	{
		LivingEntity entity = event.getEntity();
		MobStack stack = MobStack.getByNearby(entity);

		if(event.getSpawnReason().equals(SpawnReason.SPAWNER))
		{
			if (MobStack.isValid(entity.getType())) 
			{
				if (stack == null || stack.getCount() >= HardcoreSMP.getInstance().getMainConfig().getInt("MOB_STACKING.MAX_COUNT")) 
				{
					new MobStack(entity);
				} 
				else 
				{
					stack.setCount(stack.getCount() + 1);
					event.setCancelled(true);
				}
			}	
		}
	}

	@EventHandler
	public void onEntityDeathEvent(EntityDeathEvent event) 
	{
		LivingEntity entity = event.getEntity();
		MobStack stack = MobStack.getByEntity(entity);

		if (stack != null) 
		{
			if(entity.getLastDamageCause().getCause().equals(DamageCause.FALL) 
					|| entity.getLastDamageCause().getCause().equals(DamageCause.LAVA)
					|| entity.getLastDamageCause().getCause().equals(DamageCause.DROWNING)
					|| entity.getLastDamageCause().getCause().equals(DamageCause.SUFFOCATION)
					|| entity.getLastDamageCause().getCause().equals(DamageCause.FALLING_BLOCK)
					|| entity.getLastDamageCause().getCause().equals(DamageCause.HOT_FLOOR))
			{
				Random random = new Random();
				int randomInt = random.nextInt(2);
				List<ItemStack> dropList = new ArrayList<ItemStack>();
				
				for(ItemStack drops : event.getDrops())
				{
					drops.setAmount((randomInt + 1) * stack.getCount());
					dropList.add(drops);
				}
				event.getDrops().clear();
				event.getDrops().addAll(dropList);
			}

			else if(entity.getLastDamageCause().getCause().equals(DamageCause.VOID))
			{
				stack.setCount(0);
				event.getDrops().clear();
			}
			else
			{
				stack.setCount(stack.getCount() - 1);
			}

		}
	}

	@EventHandler
	public void onChunkUnloadEvent(ChunkUnloadEvent event) 
	{
		Chunk chunk = event.getChunk();
		for (Entity entity : chunk.getEntities()) 
		{
			if (entity instanceof LivingEntity  && MobStack.isValid(entity.getType())) 
			{
				MobStack stack = MobStack.getByEntity((LivingEntity) entity);
				if (stack != null) 
				{

					for (int i = 0; i < stack.getCount(); i++) 
					{
						stack.getEntity().getWorld().spawnEntity(stack.getEntity().getLocation(), stack.getEntity().getType());
					}

					stack.getEntity().remove();

					MobStack.getStacks().remove(stack);
				}
			}
		}
	}

	@EventHandler
	public void onChunkLoadEvent(ChunkLoadEvent event) 
	{
		Chunk chunk = event.getChunk();
		for (Entity entity : chunk.getEntities()) 
		{
			if (entity instanceof LivingEntity && !(entity instanceof Player) && MobStack.isValid(entity.getType())) 
			{
				MobStack stack = MobStack.getByNearby((LivingEntity) entity);

				if (stack != null && stack.getCount() < HardcoreSMP.getInstance().getMainConfig().getInt("MOB_STACKING.MAX_COUNT")) 
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

}
