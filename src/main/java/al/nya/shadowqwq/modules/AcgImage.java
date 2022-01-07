package al.nya.shadowqwq.modules;

import al.nya.shadowqwq.ShadowQwQ;
import al.nya.shadowqwq.acgimage.SomeAcg;
import al.nya.shadowqwq.annotation.Command;
import al.nya.shadowqwq.annotation.EventTarget;
import al.nya.shadowqwq.utils.FileUtil;
import al.nya.shadowqwq.utils.HTTPUtil;
import al.nya.shadowqwq.utils.command.CommandUtil;
import al.nya.shadowqwq.utils.command.DetailedArg;
import al.nya.shadowqwq.utils.command.Side;
import al.nya.shadowqwq.utils.command.Usage;
import al.nya.shadowqwq.utils.json.ACGDetail;
import al.nya.shadowqwq.utils.json.ACGList;
import al.nya.shadowqwq.utils.json.SavedInfo;
import com.google.gson.Gson;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.*;
import net.mamoe.mirai.utils.ExternalResource;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.GenericSignatureFormatError;
import java.util.*;
import java.util.logging.Logger;

@Command(prefix = "acgimage")
public class AcgImage extends Module{
    private static Usage acgUsage = new Usage(null,new ArrayList<DetailedArg>(),"Get ACG Images", Side.All);
    private static Usage forceUpdate = new Usage("update",new ArrayList<DetailedArg>(),"Force update pages list",Side.Private);
    public AcgImage() {
        super("AcgImage");
        addUsage(acgUsage);
        addUsage(forceUpdate);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 24); // 控制时
        calendar.set(Calendar.MINUTE, 0);       // 控制分
        calendar.set(Calendar.SECOND, 0);
        Timer timer = new Timer();
        Date date = calendar.getTime();
        timer.scheduleAtFixedRate(new SomeAcg(),date,1000 * 60 * 60 * 24);
    }
    @EventTarget
    public void onGroupMessage(GroupMessageEvent event){
        StringBuilder margeMessage = new StringBuilder();
        for (SingleMessage singleMessage : event.getMessage()) {
            if (!(singleMessage instanceof OnlineMessageSource)){
                margeMessage.append(singleMessage.toString());
            }
        }
        if (margeMessage.toString().equalsIgnoreCase("/acgimage")){
            event.getGroup().sendMessage(getImage(event.getGroup(),null));
        }
    }
    public MessageChain getImage(Group group, Friend friend){
        long time = System.currentTimeMillis();
        File config = new File("./acgimage/info.json");
        if (config.exists()){
            try {
                SavedInfo savedInfo = new Gson().fromJson(new String(FileUtil.readFile(config)),SavedInfo.class);
                int randomPage = new Random().nextInt(savedInfo.available);
                String list = new String(HTTPUtil.getBytes("https://someacg.rocks/api/list?page="+randomPage));
                ShadowQwQ.INSTANCE.logger.info(list);
                ACGList acgList = new Gson().fromJson(list,ACGList.class);
                int randomImage = new Random().nextInt(acgList.list.size());
                String detail = new String(HTTPUtil.getBytes("https://someacg.rocks/api/detail/"+acgList.list.get(randomImage).index));
                ShadowQwQ.INSTANCE.logger.info(detail);
                ACGDetail acgDetail = new Gson().fromJson(detail,ACGDetail.class);
                File file = new File("./acgimage/"+acgDetail.data.file_name);
                ShadowQwQ.INSTANCE.logger.info("receiving image");
                if (group != null){
                    group.sendMessage("receiving image");
                }
                if (friend != null){
                    friend.sendMessage("receiving image");
                }
                FileUtil.writeFile(file,HTTPUtil.getBytes("https://someacg.rocks/api/file/"+acgDetail.data.file_name));
                Image image = null;
                if (group != null){
                    image = group.uploadImage(ExternalResource.create(file));
                }
                if (friend != null){
                    image = friend.uploadImage(ExternalResource.create(file));
                }
                long total = System.currentTimeMillis() - time;
                MessageChainBuilder messageChainBuilder = new MessageChainBuilder();
                messageChainBuilder.add("Image from someacg.rocks\n");
                messageChainBuilder.add("https://someacg.rocks/detail/"+acgList.list.get(randomImage).index);
                messageChainBuilder.add(image);
                messageChainBuilder.add("\n Total time:"+total+"ms");
                return messageChainBuilder.asMessageChain();
            } catch (Exception e) {
                e.printStackTrace();
                long total = System.currentTimeMillis() - time;
                MessageChainBuilder messageChainBuilder = new MessageChainBuilder();
                messageChainBuilder.add(e.getMessage());
                messageChainBuilder.add("\n Total time:"+total+"ms");
                return messageChainBuilder.asMessageChain();
            }
        }else {
            long total = System.currentTimeMillis() - time;
            MessageChainBuilder messageChainBuilder = new MessageChainBuilder();
            messageChainBuilder.add("No pages config,please wait page info update");
            messageChainBuilder.add("\n Total time:"+total+"ms");
            return messageChainBuilder.asMessageChain();
        }
    }
    @EventTarget
    public void onFriendMessage(FriendMessageEvent event){
        if (CommandUtil.isUsage(event.getMessage(),forceUpdate,this,false)){
            if (event.getFriend().getId() == ShadowQwQ.owner){
                long time = System.currentTimeMillis();
                try {
                    SomeAcg.update();
                } catch (Exception e) {
                    e.printStackTrace();
                    long total = System.currentTimeMillis() - time;
                    event.getFriend().sendMessage(new StringBuilder().append("Update canceled:\n").append("Reason:").append(e.getMessage())
                            .append("\n").append("Total time:").append(total).append("ms").toString());
                    return;
                }
                long total = System.currentTimeMillis() - time;
                event.getFriend().sendMessage("Pages info updated\nTotal time:"+total+"ms");
            }
        }
    }

}