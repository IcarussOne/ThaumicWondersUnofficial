package com.verdantartifice.thaumicwonders.common.compat.groovyscript;

import com.cleanroommc.groovyscript.compat.mods.GroovyPropertyContainer;
import com.verdantartifice.thaumicwonders.common.compat.groovyscript.handlers.CatalyzationChamber;
import com.verdantartifice.thaumicwonders.common.compat.groovyscript.handlers.MeatyOrb;
import com.verdantartifice.thaumicwonders.common.compat.groovyscript.handlers.OsmoticEnchanter;

public class GSContainer extends GroovyPropertyContainer {
    public final CatalyzationChamber CatalyzationChamber = new CatalyzationChamber();
    public final MeatyOrb MeatyOrb = new MeatyOrb();
    public final OsmoticEnchanter OsmoticEnchanter = new OsmoticEnchanter();

    public GSContainer() {
        this.addProperty(CatalyzationChamber);
        this.addProperty(MeatyOrb);
        this.addProperty(OsmoticEnchanter);
    }
}
