package com.coladungeon.mod.ArknightsMod.items;

import com.coladungeon.items.weapon.melee.MeleeWeapon;

public class ArknightsWeapon extends MeleeWeapon {
    public enum ChargeType{
        Nope,
        Accu,
        Once
    }
    public ChargeType useCharge=ChargeType.Nope;
} 