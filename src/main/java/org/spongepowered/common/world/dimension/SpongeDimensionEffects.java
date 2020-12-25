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
package org.spongepowered.common.world.dimension;

import net.minecraft.world.DimensionType;
import org.spongepowered.api.ResourceKey;

public final class SpongeDimensionEffects {

    public static final SpongeDimensionEffect OVERWORLD = new SpongeDimensionEffect((ResourceKey) (Object) DimensionType.OVERWORLD_EFFECTS);

    public static final SpongeDimensionEffect NETHER = new SpongeDimensionEffect((ResourceKey) (Object) DimensionType.NETHER_EFFECTS);

    public static final SpongeDimensionEffect END = new SpongeDimensionEffect((ResourceKey) (Object) DimensionType.END_EFFECTS);

    private SpongeDimensionEffects() {
    }
}
