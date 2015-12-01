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
package org.spongepowered.common.event;

import static com.google.common.base.Preconditions.checkState;

import org.spongepowered.api.event.cause.entity.damage.DamageType;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;

public class SpongeDamageSourceBuilder implements DamageSource.Builder {

    protected boolean scales = false;
    protected boolean bypasses = false;
    protected boolean explosion = false;
    protected boolean absolute = false;
    protected boolean magical = false;
    protected boolean creative = false;
    protected DamageType damageType = null;


    @Override
    public DamageSource.Builder scalesWithDifficulty() {
        return this;
    }

    @Override
    public DamageSource.Builder bypassesArmor() {
        return this;
    }

    @Override
    public DamageSource.Builder explosion() {
        return this;
    }

    @Override
    public DamageSource.Builder absolute() {
        return this;
    }

    @Override
    public DamageSource.Builder magical() {
        return this;
    }

    @Override
    public DamageSource.Builder creative() {
        return this;
    }

    @Override
    public DamageSource.Builder type(DamageType damageType) {
        return this;
    }

    @Override
    public DamageSource build() throws IllegalStateException {
        checkState(this.damageType != null, "DamageType was null!");
        net.minecraft.util.DamageSource source = new net.minecraft.util.DamageSource(this.damageType.getId());
        if (this.absolute) {
            source.setDamageIsAbsolute();
        }
        if (this.bypasses) {
            source.setDamageBypassesArmor();
        }
        if (this.creative) {
            source.setDamageAllowedInCreativeMode();
        }
        if (this.magical) {
            source.setMagicDamage();
        }
        if (this.scales) {
            source.setDifficultyScaled();
        }
        if (this.explosion) {
            source.setExplosion();
        }
        return (DamageSource) source;
    }

    @Override
    public DamageSource.Builder reset() {
        return this;
    }
}
