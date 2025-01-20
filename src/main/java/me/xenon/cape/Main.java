package me.xenon.cape;

import me.xenon.cape.module.CustomCape;
import net.minecraftforge.fml.common.Mod;

@Mod("example")
public class Main implements Wrapper {

    public Main() {
        EVENT_BUS.register(new CustomCape());
        EVENT_BUS.register(this);
    }
}