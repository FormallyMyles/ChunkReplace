package us.Myles.ChunkReplace;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;

public class ChunkPos {
	private String world;
	private int ChunkX;
	private int ChunkZ;

	public ChunkPos(Chunk chunk) {
		this.world = chunk.getWorld().getName();
		this.ChunkX = chunk.getX();
		this.ChunkZ = chunk.getZ();
	}

	public Chunk toChunk() {
		World world = Bukkit.getWorld(this.world);
		if (world == null) {
			return null;
		}
		Chunk chunk = world.getChunkAt(this.ChunkX, this.ChunkZ);
		return (chunk != null) && (chunk.isLoaded()) ? chunk : null;
	}

	public int hashCode() {
		return new HashCodeBuilder().append(this.world).append(this.ChunkX).append(this.ChunkZ).toHashCode();
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ChunkPos)) {
			return false;
		}
		ChunkPos c = (ChunkPos) obj;

		return (c.ChunkX == this.ChunkX) && (c.ChunkZ == this.ChunkZ) && (c.world.equals(this.world));
	}
}