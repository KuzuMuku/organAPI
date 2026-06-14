package cn.kuzuanpa.organapi.api.event;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.Event;

public class OrganStateCommittedEvent extends Event {
    private final Player viewer;
    private final Entity target;
    private final String menuType;
    private final boolean dirty;

    public OrganStateCommittedEvent(Player viewer, Entity target, String menuType, boolean dirty) {
        this.viewer = viewer;
        this.target = target;
        this.menuType = menuType;
        this.dirty = dirty;
    }

    public Player getViewer() {
        return viewer;
    }

    public Entity getTarget() {
        return target;
    }

    public String getMenuType() {
        return menuType;
    }

    public boolean wasDirty() {
        return dirty;
    }
}
