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
package org.spongepowered.common.world.server;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.data.DataManipulator;
import org.spongepowered.api.data.Key;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.data.persistence.DataContainer;
import org.spongepowered.api.data.persistence.DataFormats;
import org.spongepowered.api.data.persistence.DataView;
import org.spongepowered.api.data.persistence.Queries;
import org.spongepowered.api.data.value.Value;
import org.spongepowered.api.datapack.DataPack;
import org.spongepowered.api.datapack.DataPacks;
import org.spongepowered.api.tag.Tag;
import org.spongepowered.api.util.MinecraftDayTime;
import org.spongepowered.api.world.WorldType;
import org.spongepowered.api.world.WorldTypeEffect;
import org.spongepowered.api.world.WorldTypeTemplate;
import org.spongepowered.common.SpongeCommon;
import org.spongepowered.common.data.SpongeDataManager;
import org.spongepowered.common.data.provider.DataProviderLookup;
import org.spongepowered.common.util.AbstractDataPackEntryBuilder;
import org.spongepowered.common.util.AbstractResourceKeyedBuilder;

import java.io.IOException;
import java.util.Objects;
import java.util.OptionalLong;
import java.util.function.Function;

public record SpongeWorldTypeTemplate(ResourceKey key, DimensionType dimensionType, DataPack<WorldTypeTemplate> pack) implements WorldTypeTemplate {

    @Override
    public int contentVersion() {
        return 0;
    }

    @Override
    public DataContainer toContainer() {
        final JsonElement serialized = SpongeWorldTypeTemplate.encode(this, SpongeCommon.server().registryAccess());
        try {
            final DataContainer container = DataFormats.JSON.get().read(serialized.toString());
            container.set(Queries.CONTENT_VERSION, this.contentVersion());
            return container;
        } catch (IOException e) {
            throw new IllegalStateException("Could not read deserialized DimensionType:\n" + serialized, e);
        }
    }

    @Override
    public WorldType worldType() {
        return (WorldType) (Object) this.dimensionType;
    }

    public static JsonElement encode(final WorldTypeTemplate template, final RegistryAccess registryAccess) {
        final RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, registryAccess);
        return SpongeDimensionTypes.DIRECT_CODEC.encodeStart(ops, (DimensionType) (Object) template.worldType()).getOrThrow(false, e -> {});
    }

    public static DimensionType decode(final JsonElement json, final RegistryAccess registryAccess) {
        final RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, registryAccess);
        return SpongeDimensionTypes.DIRECT_CODEC.parse(ops, json).getOrThrow(false, e -> {});
    }

    public static WorldTypeTemplate decode(final DataPack<WorldTypeTemplate> pack, final ResourceKey key, final JsonElement packEntry, final RegistryAccess registryAccess) {
        final DimensionType parsed = SpongeWorldTypeTemplate.decode(packEntry, registryAccess);
        return new SpongeWorldTypeTemplate(key, parsed, pack);
    }

    public static final class BuilderImpl extends AbstractDataPackEntryBuilder<WorldType, WorldTypeTemplate, Builder> implements WorldTypeTemplate.Builder {

        private static DataProviderLookup PROVIDER_LOOKUP = SpongeDataManager.getProviderRegistry().getProviderLookup(WorldType.class);

        private DataManipulator.Mutable manipulator = DataManipulator.mutableOf();

        public BuilderImpl() {
            this.reset();
        }

        @Override
        public Function<WorldTypeTemplate, WorldType> valueExtractor() {
            return WorldTypeTemplate::worldType;
        }

        @Override
        public <V> WorldTypeTemplate.Builder add(final Key<? extends Value<V>> key, final V value) {
            if (!PROVIDER_LOOKUP.getProvider(key).isSupported(Biome.class)) {
                throw new IllegalArgumentException(key + " is not supported for world types");
            }
            this.manipulator.set(key, value);
            return this;
        }

        @Override
        public Builder reset() {
            this.manipulator = DataManipulator.mutableOf();
            this.key = null;
            this.pack = DataPacks.WORLD_TYPE;
            final DimensionType defaultOverworld =
                    SpongeCommon.server().registryAccess().registryOrThrow(Registry.DIMENSION_TYPE_REGISTRY).get(BuiltinDimensionTypes.OVERWORLD);
            this.fromValue((WorldType) (Object) defaultOverworld);
            return this;
        }

        @Override
        public Builder fromValue(final WorldType type) {
            this.manipulator.set(type.getValues());
            return this;
        }

        @Override
        public Builder fromDataPack(final DataView pack) throws IOException {
            final JsonElement json = JsonParser.parseString(DataFormats.JSON.get().write(pack));
            // TODO catch & rethrow exceptions in CODEC?
            // TODO probably need to rewrite CODEC to allow reading after registries are frozen
            final DataResult<Holder<DimensionType>> parsed = DimensionType.CODEC.parse(JsonOps.INSTANCE, json);
            final DimensionType dimensionType = parsed.getOrThrow(false, e -> {}).value();

            this.fromValue((WorldType) (Object) dimensionType);
            return this;
        }

        @Override
        public @NonNull WorldTypeTemplate build0() {
            @Nullable final WorldTypeEffect effect = this.manipulator.getOrNull(Keys.WORLD_TYPE_EFFECT);
            final boolean scorching = this.manipulator.require(Keys.SCORCHING);
            final boolean natural = this.manipulator.require(Keys.NATURAL_WORLD_TYPE);
            final double coordinateMultiplier = this.manipulator.require(Keys.COORDINATE_MULTIPLIER);
            final boolean hasSkylight = this.manipulator.require(Keys.HAS_SKYLIGHT);
            final boolean hasCeiling = this.manipulator.require(Keys.HAS_CEILING);
            final float ambientLighting = this.manipulator.require(Keys.AMBIENT_LIGHTING);
            @Nullable final MinecraftDayTime fixedTime = this.manipulator.getOrNull(Keys.FIXED_TIME);
            final boolean bedsUsable = this.manipulator.require(Keys.BEDS_USABLE);
            final boolean respawnAnchorsUsable = this.manipulator.require(Keys.RESPAWN_ANCHOR_USABLE);
            final int floor = this.manipulator.require(Keys.WORLD_FLOOR);
            final int height = this.manipulator.require(Keys.WORLD_HEIGHT);
            final int logicalHeight = this.manipulator.require(Keys.WORLD_LOGICAL_HEIGHT);
            @Nullable final Tag<BlockType> infiniburn = this.manipulator.getOrNull(Keys.INFINIBURN);

            // TODO monstersettings
            final boolean piglinSafe = this.manipulator.require(Keys.PIGLIN_SAFE);
            final boolean hasRaids = this.manipulator.require(Keys.HAS_RAIDS);
            final UniformInt monsterSpawnLightTest = UniformInt.of(0, 7);
            final int monsterSpawnBlockLightLimit = 0;

            // final boolean createDragonFight = this.manipulator.require(Keys.CREATE_DRAGON_FIGHT);
            try {

                final DimensionType dimensionType =
                        new DimensionType(fixedTime == null ? OptionalLong.empty() : OptionalLong.of(fixedTime.asTicks().ticks()),
                                hasSkylight, hasCeiling, scorching, natural, coordinateMultiplier,
                                bedsUsable, respawnAnchorsUsable,
                                floor, height, logicalHeight,
                                (TagKey<Block>) (Object) infiniburn,
                                (ResourceLocation) (Object) effect.key(),
                                ambientLighting,
                                new DimensionType.MonsterSettings(piglinSafe, hasRaids, monsterSpawnLightTest, monsterSpawnBlockLightLimit));
                return new SpongeWorldTypeTemplate(this.key, dimensionType, this.pack);
            } catch (IllegalStateException e) { // catch and rethrow minecraft internal exception
                throw new IllegalStateException(String.format("Template '%s' was not valid!", this.key), e);
            }
        }
    }
}
