package us.Myles.ChunkReplace;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Plug extends JavaPlugin implements Listener {
	private Queue<ChunkPos> chunkQueue = new LinkedList<ChunkPos>();
	private List<ChunkPos> alreadyChecked = new ArrayList<ChunkPos>();

	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(this, this);
		// Check Already Loaded Chunks
		World w = Bukkit.getServer().getWorld("world");
		for (Chunk c : w.getLoadedChunks()) {
			if (alreadyChecked.contains(new ChunkPos(c)))
				continue;
			chunkQueue.add(new ChunkPos(c));
		}
		getServer().getScheduler().scheduleSyncRepeatingTask(this,
				new Runnable() {
					public void run() {
						int chunksPerCheck = 5;
						while (--chunksPerCheck >= 0) {
							ChunkPos poll = chunkQueue.poll();
							if (poll != null) {
								Chunk c = poll.toChunk();
								if (c != null) {
									filterChunk(c);
								}
							}
						}
					}
				}, 20L, 3);

	}

	private void filterChunk(Chunk c) {
		if (c == null)
			return; // Chunk Already Unloaded :-(
		if (alreadyChecked.contains(new ChunkPos(c)))
			return;
		// Let's be silly, ChestMinecarts -> Chests
		for (Entity e : c.getEntities()) {
			if (e instanceof StorageMinecart) {
				StorageMinecart m = (StorageMinecart) e;
				m.getLocation().getBlock().setType(Material.CHEST);
				m.getLocation().getBlock().setData((byte) 0);
				if (m.getLocation().getBlock().getState() instanceof Chest) {
					Chest chest = (Chest) m.getLocation().getBlock().getState();
					chest.getInventory().setContents(
							m.getInventory().getContents());
					m.remove();
				} else {
					System.out.println("Nope.avi");
				}
			}
		}
		int maxY = c.getWorld().getMaxHeight();
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				for (int y = 0; y < maxY; y++) {
					Block b = c.getBlock(x, y, z);
					if (!b.isEmpty()) {
						if (b.getType().equals(Material.IRON_ORE)) {
							b.setType(Material.DIAMOND_BLOCK);
						}
					}
				}
			}
		}
		System.out.println("Replaced Chunk");
		alreadyChecked.add(new ChunkPos(c));
	}

	@EventHandler
	public void chunkLoad(ChunkLoadEvent e) {
		if (e.getWorld().getName().equalsIgnoreCase("world")) {
			if (alreadyChecked.contains(new ChunkPos(e.getChunk())))
				return;
			chunkQueue.add(new ChunkPos(e.getChunk()));
		}
	}
}
