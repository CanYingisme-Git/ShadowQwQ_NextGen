package al.nya.shadowqwq.modules;

import al.nya.shadowqwq.ShadowQwQ;

import java.util.ArrayList;

public class ModuleManager {
    private static ArrayList<Module> modules = new ArrayList<Module>();
    public static void addModule(Module module){
        modules.add(module);
        ShadowQwQ.INSTANCE.logger.info("New module loaded:"+module.getName()+" Class:"+module.getClass().getName());
    }

    public static ArrayList<Module> getModules() {
        return modules;
    }
}
