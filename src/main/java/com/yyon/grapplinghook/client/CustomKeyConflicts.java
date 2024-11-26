package com.yyon.grapplinghook.client;

import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraftforge.client.settings.IKeyConflictContext;

public enum CustomKeyConflicts implements IKeyConflictContext {
    INGAME_NO_CONFLICT{
        @Override
        public boolean isActive() {
            return true;
        }

        @Override
        public boolean conflicts(IKeyConflictContext iKeyConflictContext) {
            return false;
        }
    };
}
