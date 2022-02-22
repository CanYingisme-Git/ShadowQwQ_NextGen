package al.nya.shadowqwq.analyze;

import al.nya.shadowqwq.ShadowQwQ;
import al.nya.shadowqwq.modules.Module;
import al.nya.shadowqwq.utils.command.Usage;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;

import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class AnalyzeTimer extends TimerTask {
    private Map<Module,Map<Usage,Long>> commandUses = new HashMap<Module,Map<Usage,Long>>();
    private long messagesGroup = 0;
    private long messagesFriend = 0;
    private long offline = 0;
    private Date date;
    public AnalyzeTimer(){
        date = new Date();
    }
    public void addGroupMessage(){
        synchronized (this){
            messagesGroup++;
        }
    }
    public void addOffline(){
        synchronized (this){
            offline++;
        }
    }
    public synchronized void addFriendMessage(){
        synchronized (this){
            messagesFriend++;
        }
    }
    public synchronized void addCommandUse(Module module, Usage usage){
        synchronized (this){
            AtomicBoolean stored = new AtomicBoolean(false);
            commandUses.forEach((M,L)->{
                if (M == module){
                    AtomicBoolean store = new AtomicBoolean(false);
                    L.forEach((U,LL)->{
                        if (U == usage){
                            LL += 1;
                            store.set(true);
                            stored.set(true);
                        }
                    });
                    if (!store.get()){
                        L.put(usage,1L);
                        stored.set(true);
                    }
                }
            });
            if (!stored.get()){
                commandUses.put(module, Collections.singletonMap(usage,1L));
            }
        }
    }
    public synchronized MessageChain getAnalyze(){
        StringBuilder sb = new StringBuilder();
        sb.append("Data ").append(date).append(" - ").append(new Date()).append("\n");
        sb.append("Total group message(s):").append(messagesGroup).append("\n");
        sb.append("Total friend message(s):").append(messagesFriend).append("\n");
        sb.append("Total friend message(s):").append(messagesFriend).append("\n");
        sb.append("Offline ").append(offline).append(" time(s)");
        sb.append("Total friend(s):").append(Bot.getInstances().get(0).getFriends().size()).append("\n");
        sb.append("Total group(s):").append(Bot.getInstances().get(0).getGroups().size()).append("\n");
        sb.append("command used times:").append("\n");
        commandUses.forEach((M,L)->{
            sb.append(M.getName()).append(":\n");
            L.forEach((U,LV)->{
                if (U.getName() == null){
                    sb.append("/").append(M.getName()).append(":").append(LV).append("\n");
                }else {
                    sb.append("/").append(M.getName()).append(" ").append(U.getName()).append(":").append(LV).append("\n");
                }
            });
        });
        File setu = new File("./setu");
        sb.append("Setu cached image(s):").append(setu.listFiles().length).append("\n");
        sb.append("Setu cache dir space occupation:").append(setu.getTotalSpace()/1024/1024).append("GB");
        MessageChainBuilder mb = new MessageChainBuilder();
        mb.append(sb.toString());
        return mb.asMessageChain();
    }
    @Override
    public void run() {
        MessageChain messageChain;
        synchronized (this){
            messageChain = getAnalyze();
            messagesGroup = 0;
            messagesFriend = 0;
            offline = 0;
            commandUses.clear();
            date = new Date();
        }
        Bot.getInstances().get(0).getFriend(ShadowQwQ.owner).sendMessage(messageChain);
    }
}
