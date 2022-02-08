package al.nya.shadowqwq.modules;

import al.nya.shadowqwq.ShadowQwQ;
import al.nya.shadowqwq.annotation.Command;
import al.nya.shadowqwq.annotation.EventTarget;
import al.nya.shadowqwq.utils.FileUtil;
import al.nya.shadowqwq.utils.command.CommandUtil;
import al.nya.shadowqwq.utils.command.DetailedArg;
import al.nya.shadowqwq.utils.command.Side;
import al.nya.shadowqwq.utils.command.Usage;
import al.nya.shadowqwq.utils.event.EventProcessor;
import al.nya.shadowqwq.utils.json.bot.BlackList;
import com.google.gson.Gson;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.MemberPermission;
import net.mamoe.mirai.event.Event;
import net.mamoe.mirai.event.events.*;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

@Command(prefix = "core")
public class CoreService extends Module{
    private Usage modulesUsage = new Usage("modules",new ArrayList<DetailedArg>(),"Get loaded modules", Side.All);
    private Usage leaveUsage = new Usage("leave",new ArrayList<DetailedArg>(),"Order bot leave your group",Side.Group);
    private Usage addUsage;
    public CoreService() {
        super("CoreService");
        addUsage(modulesUsage);
        ArrayList<DetailedArg> detailedArgs = new ArrayList<DetailedArg>();
        detailedArgs.add(new DetailedArg(Long.class,"A"));
        detailedArgs.add(new DetailedArg(Long.class,"B"));
        addUsage = new Usage("add",detailedArgs,"Add A and B",Side.Group);
        addUsage(addUsage);
        addUsage(leaveUsage);
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
            try{
                List<Object> objects = CommandUtil.fastParse(event.getMessage(),addUsage);
                Long calc = (Long) objects.get(0) + (Long) objects.get(1);
                event.getGroup().sendMessage(calc+"");
            }catch (Exception e){
                e.printStackTrace();
                event.getGroup().sendMessage(e.getMessage());
            }
        }
        if (CommandUtil.isUsage(event.getMessage(),modulesUsage,this,false)){
            event.getGroup().sendMessage(modules());
        }
        if (CommandUtil.isUsage(event.getMessage(),leaveUsage,this,false)){
            if (event.getSender().getPermission() == MemberPermission.ADMINISTRATOR ||event.getSender().getPermission() == MemberPermission.OWNER ){
                event.getGroup().quit();
                addBlackList(event.getGroup());
                event.getBot().getFriend(ShadowQwQ.owner).sendMessage
                        ("Bot "+event.getBot().getId()+" Ordered to leave the group "+event.getGroup()+"|"+event.getGroup().getName()+"\nCommander:"+event.getSender().getId()+"|"+event.getSender().getNick());
            }
        }

    }
    private void addBlackList(Group group){
        try {
            File file = new File("./blackList.json");
            if (!file.exists()) {
                FileUtil.writeFile(file, new Gson().toJson(new BlackList()).getBytes(StandardCharsets.UTF_8));
            }
            BlackList blackList = new Gson().fromJson(new String(FileUtil.readFile(file)),BlackList.class);
            blackList.add(group);
            FileUtil.writeFile(file, new Gson().toJson(blackList).getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private boolean isBlackList(long group){
        try {
            File file = new File("./blackList.json");
            if (!file.exists()) {
                FileUtil.writeFile(file, new Gson().toJson(new BlackList()).getBytes(StandardCharsets.UTF_8));
            }
            BlackList blackList = new Gson().fromJson(new String(FileUtil.readFile(file)),BlackList.class);
            for (Long blackListGroup : blackList.getGroups()) {
                if (blackListGroup == group){
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
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
                stringBuilder.append("\n").append(module.getName());
                for (Usage usage : module.getUsages()) {
                    if (usage.getSide() == side || usage.getSide() == Side.All){
                        if (usage.getName() != null){
                            stringBuilder.append("\n").append("/").append(module.getCommand()).append(" ").append(usage.getName()).append(" ");
                            for (DetailedArg arg : usage.getArgs()) {
                                stringBuilder.append("[").append(arg.getDesc()).append("(").append(arg.getClazz().getSimpleName()).append(")] ");
                            }
                            stringBuilder.append("-").append(usage.getDesc());
                        }else {
                            stringBuilder.append("\n").append("/").append(module.getCommand()).append(" ");
                            for (DetailedArg arg : usage.getArgs()) {
                                stringBuilder.append("[").append(arg.getDesc()).append("(").append(arg.getClazz().getSimpleName()).append(")] ");
                            }
                            stringBuilder.append("-").append(usage.getDesc());
                        }
                    }
                }
            }
        }
        return stringBuilder.toString();
    }
    @EventTarget
    public void autoAcceptGroup(BotInvitedJoinGroupRequestEvent event){
        if (!isBlackList(event.getGroupId())){
            event.accept();
        }else {
            event.ignore();
        }
    }
    @EventTarget
    public void autoAcceptFriend(NewFriendRequestEvent event){
        event.accept();
    }
    @EventTarget
    public void nudge(NudgeEvent event) {
        if (event.getTarget() == event.getBot()){
            event.getFrom().nudge().sendTo(event.getSubject());
        }
    }
}
