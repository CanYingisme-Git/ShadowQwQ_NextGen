package al.nya.shadowqwq;

import al.nya.shadowqwq.annotation.EventTarget;
import al.nya.shadowqwq.modules.*;
import al.nya.shadowqwq.webhook.WebHookHttpServer;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import net.mamoe.mirai.event.Event;
import net.mamoe.mirai.event.EventChannel;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.utils.MiraiLogger;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class ShadowQwQ extends JavaPlugin {
    public static String ID = "al.nya.shadowqwq";
    public static long owner = Long.parseLong("3192799549");
    public static String VERSION = "0.1";
    public static VersionType VERSION_TYPE = VersionType.ALPHA;
    public MiraiLogger logger = this.getLogger();
    public static ShadowQwQ INSTANCE;
    public static enum VersionType{
        RELEASE,
        ALPHA
    }
    public ShadowQwQ() {
        super(new JvmPluginDescriptionBuilder(ID,VERSION).info("A QQ BOT").name("ShadowQwQ").build());
        INSTANCE = this;
    }
    @Override
    public void onEnable(){
        logger.info("ShadowQwQ enabled");
        ModuleManager.addModule(new CoreService());
        ModuleManager.addModule(new Minecraft());
        ModuleManager.addModule(new Misc());
        ModuleManager.addModule(new AcgImage());
        ModuleManager.addModule(new GithubWebhook());
        ModuleManager.addModule(new Broadcast());
        ModuleManager.addModule(new Setu());
        new WebHookHttpServer(1299);
        EventChannel<Event> eventChannel = GlobalEventChannel.INSTANCE.parentScope(this);
        for (Module module : ModuleManager.getModules()) {
            Method[] methods = module.getClass().getMethods();
            Method[] methodsD = methods.getClass().getDeclaredMethods();
            List<Method> methodList = new ArrayList<Method>(Arrays.asList(methods));
            Arrays.asList(methodsD).forEach(M -> {
                for (Method method : methodList) {
                    if (method.getName().equals(M.getName())){
                        return;
                    }
                }
                methodList.add(M);
            });
            for (Method method : methodList) {
                for (Annotation declaredAnnotation : method.getDeclaredAnnotations()) {
                    if (declaredAnnotation.annotationType() == EventTarget.class){
                        if (method.getParameters().length == 1){
                            method.setAccessible(true);
                            eventChannel.subscribeAlways((Class<? extends Event>) method.getParameters()[0].getType(), new Consumer<Event>() {
                                @Override
                                public void accept(Event event) {
                                    try {
                                        method.invoke(module,event);
                                    } catch (IllegalAccessException | InvocationTargetException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            logger.info("Register Event:"+method.getParameters()[0].getType().getName()+" to " + module.getName()+"."+method.getName());
                        }
                    }
                }
            }
        }
    }
}
