package al.nya.shadowqwq.acgimage;

import al.nya.shadowqwq.ShadowQwQ;
import al.nya.shadowqwq.utils.FileUtil;
import al.nya.shadowqwq.utils.HTTPUtil;
import al.nya.shadowqwq.utils.json.acg.ACGList;
import al.nya.shadowqwq.utils.json.SavedInfo;
import com.google.gson.Gson;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Friend;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.TimerTask;

public class SomeAcg extends TimerTask {
    @Override
    public void run() {
        long time = System.currentTimeMillis();
        try {
            update();
        } catch (IOException e) {
            e.printStackTrace();
            long total = System.currentTimeMillis() - time;
            sendCancelMessage(e,total);
            return;
        }
        long total = System.currentTimeMillis() - time;
        sendFinishMessage(total);
    }
    public static void sendCancelMessage(Exception e,long time){
        for (Bot instance : Bot.getInstances()) {
            Friend friend = instance.getFriend(ShadowQwQ.owner);
            if (friend != null){
                friend.sendMessage(new StringBuilder().append("Update canceled:\n").append("Reason:").append(e.getMessage())
                        .append("\n").append("Total time:").append(time).append("ms").toString());
            }
        }
    }
    public static void sendFinishMessage(long time){
        for (Bot instance : Bot.getInstances()) {
            Friend friend = instance.getFriend(ShadowQwQ.owner);
            if (friend != null){
                File config = new File("./acgimage/info.json");
                try {
                    SavedInfo savedInfo = new Gson().fromJson(new String(FileUtil.readFile(config)),SavedInfo.class);
                    friend.sendMessage("Pages info updated\nTotal pages:"+savedInfo.available+" Total time:"+time+"ms");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public static void update() throws IOException {
        //https://someacg.rocks/api/list
        int maxList = 0;
        boolean more = true;
        while (more){
            maxList += 1;
            String list = HTTPUtil.get("https://someacg.rocks/api/list?page="+maxList);
            ACGList acgList = new Gson().fromJson(list,ACGList.class);
            more = acgList.body.size() != 0;
        }
        new File("./acgimage").mkdir();
        SavedInfo savedInfo = new SavedInfo(maxList);
        FileUtil.writeFile(new File("./acgimage/info.json"),new Gson().toJson(savedInfo).getBytes(StandardCharsets.UTF_8));
    }
}
