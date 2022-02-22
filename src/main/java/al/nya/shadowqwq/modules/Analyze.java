package al.nya.shadowqwq.modules;

import al.nya.shadowqwq.analyze.AnalyzeTimer;
import al.nya.shadowqwq.annotation.EventTarget;
import al.nya.shadowqwq.utils.command.CommandArg;
import al.nya.shadowqwq.utils.command.CommandUtil;
import al.nya.shadowqwq.utils.command.Side;
import al.nya.shadowqwq.utils.command.Usage;
import net.mamoe.mirai.event.events.BotOfflineEvent;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;

import java.util.*;

public class Analyze extends Module{
    AnalyzeTimer analyzeTimer = new AnalyzeTimer();
    private Usage getDataUsage = new Usage("getdata",new ArrayList<CommandArg>(),"Get analyze data", Side.All);
    public Analyze() {
        super("Analyze");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 24);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Timer timer = new Timer();
        Date date = calendar.getTime();
        timer.scheduleAtFixedRate(analyzeTimer,date,1000 * 60 * 60 * 24);
        addUsage(getDataUsage);
    }
    @EventTarget
    public void onGroupMessage(GroupMessageEvent evt){
        analyzeTimer.addGroupMessage();
        if (CommandUtil.isUsage(evt.getMessage(),getDataUsage,this,false)){
            evt.getGroup().sendMessage(analyzeTimer.getAnalyze());
        }
    }
    @EventTarget
    public void onFriendMessage(FriendMessageEvent evt){
        analyzeTimer.addFriendMessage();
        if (CommandUtil.isUsage(evt.getMessage(),getDataUsage,this,false)){
            evt.getFriend().sendMessage(analyzeTimer.getAnalyze());
        }
    }
    public void onOffline(BotOfflineEvent evt){

    }
    public void onCommandHit(Module module,Usage usage){
        analyzeTimer.addCommandUse(module,usage);
    }
}
