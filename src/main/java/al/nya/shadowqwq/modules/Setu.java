package al.nya.shadowqwq.modules;

import al.nya.shadowqwq.ShadowQwQ;
import al.nya.shadowqwq.annotation.Command;
import al.nya.shadowqwq.annotation.EventTarget;
import al.nya.shadowqwq.utils.FileUtil;
import al.nya.shadowqwq.utils.HTTPUtil;
import al.nya.shadowqwq.utils.command.*;
import al.nya.shadowqwq.utils.json.lolicon.LoliconAPI;
import al.nya.shadowqwq.utils.json.lolicon.LoliconData;
import al.nya.shadowqwq.utils.json.lolicon.LoliconUrl;
import com.google.gson.Gson;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.utils.ExternalResource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Command(prefix = "setu")
public class Setu extends Module {
    private Usage licenseUsage = new Usage("license",new ArrayList<CommandArg>(),"Agree this group use Setu command",Side.Group);
    private Usage delicenseUsage = new Usage("delicense",new ArrayList<CommandArg>(),"Disagree this group use Setu command",Side.Group);
    private Usage getUsage = new Usage("get",
            Arrays.asList(new EnumArg(Type.values(),"Type")),"Get AcgImage",Side.Group);
    private Usage getUsageWithTag = new Usage("get",
            Arrays.asList(new EnumArg(Type.values(),"Type"),new DetailedArg(String.class,"Tag")),"Get AcgImage Tag usage \",\" split",Side.Group);
    public Setu() {
        super("Setu");
        addUsage(licenseUsage);
        addUsage(delicenseUsage);
        addUsage(getUsage);
        addUsage(getUsageWithTag);
    }
    @EventTarget
    public void onGroup(GroupMessageEvent event){
        if (CommandUtil.isUsage(event.getMessage(),licenseUsage,this,false) && event.getSender().getId() == ShadowQwQ.owner){
            try {
                File file = new File("./setu.txt");
                if (file.exists()){
                    String s = new String(FileUtil.readFile(file));
                    List<String> groups = new ArrayList<String>(Arrays.asList(s.split("_")));
                    if (groups.get(groups.size()-1).equalsIgnoreCase("")){
                        groups.remove(groups.size()-1);
                    }
                    for (String group : groups) {
                        if (group.equalsIgnoreCase(String.valueOf(event.getGroup().getId()))){
                            event.getGroup().sendMessage("This group has been added");
                            return;
                        }
                    }
                    List<String> agroups = new ArrayList<String>();
                    agroups.add(String.valueOf(event.getGroup().getId()));
                    agroups.addAll(groups);
                    s = "";
                    for (String group : agroups) {
                        s += group + "_";
                    }
                    FileUtil.writeFile(file,s.getBytes(StandardCharsets.UTF_8));
                    event.getGroup().sendMessage("Successfully added");
                }else {
                    FileUtil.writeFile(file,(String.valueOf(event.getGroup().getId())).getBytes(StandardCharsets.UTF_8));
                    event.getGroup().sendMessage("Successfully added");
                }
            } catch (Exception e) {
                e.printStackTrace();
                event.getGroup().sendMessage(e.toString());
            }
        }
        if (CommandUtil.isUsage(event.getMessage(),delicenseUsage,this,false) && event.getSender().getId() == ShadowQwQ.owner){
            try {
                File file = new File("./setu.txt");
                if (file.exists()){
                    String s = new String(FileUtil.readFile(file));
                    List<String> groups = new ArrayList<String>(Arrays.asList(s.split("_")));
                    if (groups.get(groups.size()-1).equalsIgnoreCase("")){
                        groups.remove(groups.size()-1);
                    }
                    List<String> agroups = new ArrayList<String>(groups);
                    agroups.removeIf(group -> group.equalsIgnoreCase(String.valueOf(event.getGroup().getId())));
                    s = "";
                    for (String group : agroups) {
                        s += group + "_";
                    }
                    FileUtil.writeFile(file,s.getBytes(StandardCharsets.UTF_8));
                    event.getGroup().sendMessage("Successfully removed");
                }else {
                    event.getGroup().sendMessage("Profile does not exist");
                }
            } catch (Exception e) {
                e.printStackTrace();
                event.getGroup().sendMessage(e.toString());
            }
        }

        if (CommandUtil.isUsage(event.getMessage(),getUsageWithTag,this,false)){
            List<Object> objects = CommandUtil.fastParse(event.getMessage(),getUsageWithTag);
            sendImage(event.getGroup(),(Type) objects.get(0),(String) objects.get(1));
        }
        if (CommandUtil.isUsage(event.getMessage(),getUsage,this,false)){
            List<Object> objects = CommandUtil.fastParse(event.getMessage(),getUsage);
            sendImage(event.getGroup(),(Type) objects.get(0),null);
        }
    }
    public void sendImage(Group group,Type type,String tag){
        try{
            long time = System.currentTimeMillis();
            File file = new File("./setu.txt");
            String s = new String(FileUtil.readFile(file));
            List<String> groups = new ArrayList<String>(Arrays.asList(s.split("_")));
            for (String group1 : groups) {
                if (group1.equalsIgnoreCase(String.valueOf(group.getId()))){
                    {
                        String url = "https://api.lolicon.app/setu/v2?r18="+type.value+"&num=1";
                        if (tag != null){
                            for (String s1 : tag.split(",")) {
                                url+="&tag="+s1;
                            }
                        }
                        url+="&proxy=i.pximg.net";
                        String s2 = HTTPUtil.get(url);
                        ShadowQwQ.INSTANCE.logger.info(s2);
                        LoliconAPI loliconAPI = new Gson().fromJson(s2,LoliconAPI.class);
                        if (!loliconAPI.error.equals("")){
                            group.sendMessage(loliconAPI.error);
                            return;
                        }
                        group.sendMessage("Receiving image");
                        LoliconData loliconData = loliconAPI.data.get(0);
                        LoliconUrl loliconUrl = loliconData.urls;
                        MessageChainBuilder msg = new MessageChainBuilder();
                        StringBuilder sb = new StringBuilder();
                        sb.append("Image from Pixiv.net\n");
                        sb.append("API:Lolicon API\n");
                        sb.append("Title:").append(loliconData.title).append("\n");
                        sb.append("Author:").append(loliconData.author).append("\n");
                        sb.append("R18:").append(loliconData.r18).append("'n");
                        sb.append("Tags:");
                        for (String tag1 : loliconData.tags) {
                            sb.append(tag1).append(",");
                        }
                        sb.append("\n");
                        sb.append("OriginalUrl:").append("https://www.pixiv.net/artworks/").append(loliconData.pid).append("\n");
                        new File("./setu").mkdir();
                        String saveName = loliconUrl.original.split("/")[loliconUrl.original.split("/").length-1];
                        byte[] bytes = HTTPUtil.getPixivBytes(loliconData.urls.original);
                        FileUtil.writeFile(new File("./setu/"+saveName),bytes);
                        Image image = group.uploadImage(ExternalResource.create(new File("./setu/"+saveName)));
                        msg.add(sb.toString());
                        msg.add(image);
                        long total = System.currentTimeMillis() - time;
                        msg.add("Total time:"+total+"ms");
                    }
                    return;
                }
            }
        }catch (Exception e){
            e.getStackTrace();
            group.sendMessage(e.toString());
        }
    }
    enum Type{
        Normal(0),
        R18(1),
        Mix(2)
        ;
        private int value;
        Type(int i){
            this.value = i;
        }

        public int getValue() {
            return value;
        }
    }
}
