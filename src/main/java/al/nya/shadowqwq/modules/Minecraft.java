package al.nya.shadowqwq.modules;

import al.nya.shadowqwq.annotation.Command;
import al.nya.shadowqwq.annotation.EventTarget;
import al.nya.shadowqwq.utils.HTTPUtil;
import al.nya.shadowqwq.utils.command.CommandUtil;
import al.nya.shadowqwq.utils.command.DetailedArg;
import al.nya.shadowqwq.utils.command.Side;
import al.nya.shadowqwq.utils.command.Usage;
import al.nya.shadowqwq.utils.json.MinecraftInfo;
import al.nya.shadowqwq.utils.json.MinecraftProfile;
import com.google.gson.Gson;
import net.mamoe.mirai.event.events.GroupMessageEvent;

import java.util.Arrays;
import java.util.Base64;

@Command(prefix = "minecraft")
public class Minecraft extends Module{
    private Usage capeUsage = new Usage("cape", Arrays.asList(new DetailedArg(String.class,"Username")),"Detection capes", Side.All);
    public Minecraft() {
        super("Minecraft");
        addUsage(capeUsage);
    }
    @EventTarget
    public void onGroupMessage(GroupMessageEvent groupMessageEvent){
        if (CommandUtil.isUsage(groupMessageEvent.getMessage(),capeUsage,this,true)){
            String name = (String) CommandUtil.fastParse(groupMessageEvent.getMessage(),capeUsage).get(0);
            groupMessageEvent.getGroup().sendMessage(cape(name));
        }
    }
    private String cape(String name){
        try{
            StringBuilder stringBuilder = new StringBuilder();
            Gson gson = new Gson();
            MinecraftInfo minecraftInfo = gson.fromJson(HTTPUtil.get("https://api.mojang.com/users/profiles/minecraft/"+name),MinecraftInfo.class);
            String optifine;
            try {
                optifine = HTTPUtil.get("http://s.optifine.net/capes/"+minecraftInfo.name+".png");
            }catch (Exception e){
                optifine = "NOT FOUND";
            }
            MinecraftProfile minecraftProfile = gson.fromJson(HTTPUtil.get("https://sessionserver.mojang.com/session/minecraft/profile/"+minecraftInfo.id),MinecraftProfile.class);
            MinecraftProfile minecraftProfile1 = gson.fromJson(new String(Base64.getDecoder().decode(minecraftProfile.properties.get(0).value)),MinecraftProfile.class);
            boolean mojong = minecraftProfile1.textures.CAPE != null;
            stringBuilder.append(minecraftInfo.name).append("的披风信息\n");
            if (!optifine.equals("NOT FOUND")){
                stringBuilder.append("拥有Optifine披风\n");
            }else {
                stringBuilder.append("未拥有Optifine披风\n");
            }
            if (mojong){
                stringBuilder.append("拥有Mojang披风\n");
            }else {
                stringBuilder.append("未拥有Mojang披风\n");
            }
            return stringBuilder.toString();
        }catch (Exception e){
            e.printStackTrace();
            return e.getMessage();
        }
    }
}
