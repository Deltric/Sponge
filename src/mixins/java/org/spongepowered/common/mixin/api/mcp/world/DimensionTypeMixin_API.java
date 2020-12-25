/*
 * This file is part of Sponge, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.spongepowered.common.mixin.api.mcp.world;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.DimensionType;
import net.minecraft.world.biome.IBiomeMagnifier;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.util.MinecraftDayTime;
import org.spongepowered.api.world.biome.BiomeFinder;
import org.spongepowered.api.world.dimension.DimensionEffect;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.common.SpongeCommon;
import org.spongepowered.common.registry.provider.DimensionEffectProvider;
import org.spongepowered.common.util.SpongeMinecraftDayTime;

import java.util.Optional;
import java.util.OptionalLong;

@Mixin(DimensionType.class)
@Implements(@Interface(iface = org.spongepowered.api.world.dimension.DimensionType.class, prefix = "dimensionType$"))
public abstract class DimensionTypeMixin_API implements org.spongepowered.api.world.dimension.DimensionType {

    // @formatter:off
    @Shadow @Final private ResourceLocation effectsLocation;
    @Shadow @Final private float ambientLight;
    @Shadow @Final private OptionalLong fixedTime;
    @Shadow public abstract IBiomeMagnifier shadow$getBiomeZoomer();

    @Shadow public abstract boolean shadow$ultraWarm();
    @Shadow public abstract boolean shadow$natural();
    @Shadow public abstract double shadow$coordinateScale();
    @Shadow public abstract boolean shadow$hasSkyLight();
    @Shadow public abstract boolean shadow$hasCeiling();
    @Shadow public abstract boolean shadow$piglinSafe();
    @Shadow public abstract boolean shadow$bedWorks();
    @Shadow public abstract boolean shadow$respawnAnchorWorks();
    @Shadow public abstract boolean shadow$hasRaids();
    @Shadow public abstract int shadow$logicalHeight();
    @Shadow public abstract boolean shadow$createDragonFight();
    // @formatter:on

    @Nullable private Context api$context;

    @Override
    public Context getContext() {
        if (this.api$context == null) {
            final ResourceLocation key = SpongeCommon.getServer().registryAccess().dimensionTypes().getKey((DimensionType) (Object) this);
            this.api$context = new Context(Context.DIMENSION_KEY, key.getPath());
        }

        return this.api$context;
    }

    @Override
    public DimensionEffect effect() {
        @Nullable final DimensionEffect effect = DimensionEffectProvider.INSTANCE.get((ResourceKey) (Object) this.effectsLocation);
        if (effect == null) {
            throw new IllegalStateException(String.format("The effect '%s' has not been registered!", this.effectsLocation));
        }
        return effect;
    }

    @Override
    public BiomeFinder biomeFinder() {
        return (BiomeFinder) (Object) this.shadow$getBiomeZoomer();
    }

    @Override
    public boolean scorching() {
        return this.shadow$ultraWarm();
    }

    @Intrinsic
    public boolean dimensionType$natural() {
        return this.shadow$natural();
    }

    @Override
    public double coordinateMultiplier() {
        return this.shadow$coordinateScale();
    }

    @Override
    public boolean hasSkylight() {
        return this.shadow$hasSkyLight();
    }

    @Intrinsic
    public boolean dimensionType$hasCeiling() {
        return this.shadow$hasCeiling();
    }

    @Override
    public float ambientLighting() {
        return this.ambientLight;
    }

    @Override
    public Optional<MinecraftDayTime> fixedTime() {
        final OptionalLong fixedTime = this.fixedTime;
        if (!fixedTime.isPresent()) {
            return Optional.empty();
        }
        return Optional.of(new SpongeMinecraftDayTime(fixedTime.getAsLong()));
    }

    @Intrinsic
    public boolean dimensionType$piglinSafe() {
        return this.shadow$piglinSafe();
    }

    @Override
    public boolean bedsUsable() {
        return this.shadow$bedWorks();
    }

    @Override
    public boolean respawnAnchorsUsable() {
        return this.shadow$respawnAnchorWorks();
    }

    @Intrinsic
    public boolean dimensionType$hasRaids() {
        return this.shadow$hasRaids();
    }

    @Intrinsic
    public int dimensionType$logicalHeight() {
        return this.shadow$logicalHeight();
    }

    @Intrinsic
    public boolean dimensionType$createDragonFight() {
        return this.shadow$createDragonFight();
    }
}
