package al.nya.shadowqwq.modules;

import al.nya.shadowqwq.ShadowQwQ;
import al.nya.shadowqwq.annotation.EventTarget;
import al.nya.shadowqwq.utils.HTTPUtil;
import al.nya.shadowqwq.utils.command.DetailedArg;
import al.nya.shadowqwq.utils.command.Side;
import al.nya.shadowqwq.utils.command.Usage;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.internal.message.OnlineFriendImageImpl;
import net.mamoe.mirai.message.data.*;
import net.mamoe.mirai.utils.ExternalResource;

import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

public class Broadcast extends Module{
    private Usage usage = new Usage(null, Arrays.asList(new DetailedArg(String.class,"text")),"Broadcast", Side.Private);
    public Broadcast() {
        super("Broadcast");
    }
    @EventTarget
    public void onFriendMessage(FriendMessageEvent event){
        StringBuilder margeMessage = new StringBuilder();
        for (SingleMessage singleMessage : event.getMessage()) {
            if (!(singleMessage instanceof OnlineMessageSource)){
                margeMessage.append(singleMessage.toString());
            }
        }
        if (margeMessage.toString().startsWith("/broadcast")){
            if (event.getFriend().getId() == ShadowQwQ.owner){
                event.getFriend().sendMessage(broadcast(event.getMessage(),event.getBot()));
                return;
            }
        }
    }
    private MessageChain broadcast(MessageChain messageChain,Bot bot){
        long time = System.currentTimeMillis();
        MessageChainBuilder messageChainBuilder = new MessageChainBuilder();
        for (SingleMessage singleMessage : messageChain) {
            if (singleMessage instanceof PlainText){
                messageChainBuilder.add(singleMessage.toString().replace("/broadcast",""));
            }else if (singleMessage instanceof OnlineFriendImageImpl){
                messageChainBuilder.add(singleMessage);
            }else if (singleMessage instanceof Face){
                messageChainBuilder.add(singleMessage);
            }
        }
        for (SingleMessage singleMessage : messageChainBuilder.asMessageChain()) {
            ShadowQwQ.INSTANCE.logger.info(singleMessage.getClass()+" "+singleMessage.toString());
        }
        int[] groups = send(messageChainBuilder.asMessageChain(),bot);
        MessageChainBuilder send = new MessageChainBuilder();
        send.add("Finish send broadcast\n");
        send.add("Total groups:"+groups[0]);
        send.add("\nSuccess:"+groups[1]);
        send.add("\nFail:"+groups[2]);
        long total = System.currentTimeMillis() - time;
        send.add("\nTotal time:"+total+"ms");
        return send.asMessageChain();
    }
    private int[] send(MessageChain messageChain,Bot bot){
        int[] result = new int[3];
        result[0] = bot.getGroups().size();

        for (Group group : bot.getGroups()) {
            try{
                group.sendMessage(checkMessage(messageChain,group));
                result[1] += 1;
            }catch (Exception e){
                result[2] += 1;
            }
            try {
                Thread.sleep(700+new Random().nextInt(200));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
    private MessageChain checkMessage(MessageChain messageChain,Group group) throws IOException {
        MessageChainBuilder messageChainBuilder = new MessageChainBuilder();
        for (SingleMessage singleMessage : messageChain) {
            if (!(singleMessage instanceof OnlineFriendImageImpl)){
                messageChainBuilder.add(singleMessage);
            }else {
                ShadowQwQ.INSTANCE.logger.info("Upload img"+((OnlineFriendImageImpl) singleMessage).getOriginUrl());
                Image image = group.uploadImage(ExternalResource.create(HTTPUtil.getBytes(((OnlineFriendImageImpl) singleMessage).getOriginUrl())));
                messageChainBuilder.add(image);
            }
        }
        return messageChainBuilder.asMessageChain();
    }
}
