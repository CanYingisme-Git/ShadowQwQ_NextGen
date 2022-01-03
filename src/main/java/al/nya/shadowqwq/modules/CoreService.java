package al.nya.shadowqwq.modules;

import al.nya.shadowqwq.ShadowQwQ;
import al.nya.shadowqwq.annotation.Command;
import al.nya.shadowqwq.annotation.EventTarget;
import al.nya.shadowqwq.utils.command.CommandUtil;
import al.nya.shadowqwq.utils.command.DetailedArg;
import al.nya.shadowqwq.utils.command.Side;
import al.nya.shadowqwq.utils.command.Usage;
import al.nya.shadowqwq.utils.event.EventProcessor;
import net.mamoe.mirai.event.Event;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Command(prefix = "core")
public class CoreService extends Module{
    private Usage modulesUsage = new Usage("modules",new ArrayList<DetailedArg>(),"Get loaded modules", Side.All);
    private Usage addUsage;
    public CoreService() {
        super("CoreService");
        addUsage(modulesUsage);
        ArrayList<DetailedArg> detailedArgs = new ArrayList<DetailedArg>();
        detailedArgs.add(new DetailedArg(Integer.class,"A"));
        detailedArgs.add(new DetailedArg(Integer.class,"B"));
        addUsage = new Usage("add",detailedArgs,"Add A and B",Side.Group);
        addUsage(addUsage);
    }
    @EventTarget
    public void onFriend(FriendMessageEvent event) {
        if (event.getMessage().size() == 2){
            if (event.getMessage().get(1).toString().equals("/help")){
                event.getFriend().sendMessage(help(Side.Private));
            }
        }
        if (CommandUtil.isUsage(event.getMessage(),modulesUsage,this,false)){
            event.getFriend().sendMessage(modules());
        }
    }
    @EventTarget
    public void onGroup(GroupMessageEvent event) {
        if (event.getMessage().size() == 2){
            if (event.getMessage().get(1).toString().equals("/help")){
                event.getGroup().sendMessage(help(Side.Group));
            }
        }
        if (CommandUtil.isUsage(event.getMessage(),addUsage,this,false)){
            List<Object> objects = CommandUtil.fastParse(event.getMessage(),addUsage);
            int calc = (int)objects.get(0) + (int)objects.get(1);
            event.getGroup().sendMessage(calc+"");
        }
        if (CommandUtil.isUsage(event.getMessage(),modulesUsage,this,false)){
            event.getGroup().sendMessage(modules());
        }
    }
    private String modules(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("ShadowQwQ -Modules");
        for (Module module : ModuleManager.getModules()) {
            stringBuilder.append("\n").append(module.getClass().getName()).append(" -").append(module.getName());
        }
        return stringBuilder.toString();
    }
    private String help(Side side){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("ShadowQwQ -Help");
        stringBuilder.append("\n");
        for (Module module : ModuleManager.getModules()) {
            if (module.hasCommand()){
                stringBuilder.append(module.getName());
                for (Usage usage : module.getUsages()) {
                    if (usage.getSide() == side || usage.getSide() == Side.All){
                        stringBuilder.append("\n").append("/").append(module.getCommand()).append(" ").append(usage.getName()).append(" ");
                        for (DetailedArg arg : usage.getArgs()) {
                            stringBuilder.append("[").append(arg.getDesc()).append("(").append(arg.getClazz().getSimpleName()).append(")] ");
                        }
                        stringBuilder.append("-").append(usage.getDesc());
                    }
                }
            }
        }
        return stringBuilder.toString();
    }
}
