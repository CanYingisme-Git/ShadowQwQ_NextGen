package al.nya.shadowqwq.modules;

import al.nya.shadowqwq.ShadowQwQ;
import al.nya.shadowqwq.annotation.Command;
import al.nya.shadowqwq.annotation.EventTarget;
import al.nya.shadowqwq.utils.EnumEventType;
import al.nya.shadowqwq.utils.FileUtil;
import al.nya.shadowqwq.utils.command.CommandUtil;
import al.nya.shadowqwq.utils.command.DetailedArg;
import al.nya.shadowqwq.utils.command.Side;
import al.nya.shadowqwq.utils.command.Usage;
import al.nya.shadowqwq.utils.json.github.*;
import com.google.gson.Gson;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Command(prefix = "github")
public class GithubWebhook extends Module {
    private Usage addUsage = new Usage("add", Arrays.asList(new DetailedArg(String.class,"RepositoryFullName")),"Subscribe a webhook" ,Side.Group);
    private Usage removeUsage = new Usage("remove", Arrays.asList(new DetailedArg(String.class,"RepositoryFullName")),"Unsubscribe a webhook" ,Side.Group);
    public GithubWebhook() {
        super("GithubWebhook");
        addUsage(addUsage);
        addUsage(removeUsage);
    }
    @EventTarget
    public void onGroupMessage(GroupMessageEvent event){
        if (CommandUtil.isUsage(event.getMessage(),addUsage,this,false)){
            List<Object> objects = CommandUtil.fastParse(event.getMessage(),addUsage);
            event.getGroup().sendMessage(addSub(event.getGroup(), (String) objects.get(0)));
        }
        else if (CommandUtil.isUsage(event.getMessage(),addUsage,this,false)){
            List<Object> objects = CommandUtil.fastParse(event.getMessage(),addUsage);
            event.getGroup().sendMessage(removeSub(event.getGroup(), (String) objects.get(0)));
        }
    }
    public static void send(WebHookInfo webHookInfo, EnumEventType eventType){
        MessageChainBuilder messageChainBuilder = new MessageChainBuilder();
        if (eventType == EnumEventType.ping){
            messageChainBuilder.add("Github - Ping\n");
            if (webHookInfo == null){
                ShadowQwQ.INSTANCE.logger.info("NULL");
            }
            messageChainBuilder.add("Repo:"+webHookInfo.repository.full_name+"\n");
            messageChainBuilder.add("zen:"+webHookInfo.zen+"\n");
            messageChainBuilder.add("Seeing this message proves that the WebHook configuration is successful");
        }else if (eventType == EnumEventType.push){
            messageChainBuilder.add("Github - Push\n");
            messageChainBuilder.add("Repository:"+webHookInfo.repository.full_name+"\n");
            messageChainBuilder.add("Branch:"+webHookInfo.ref.replace("refs/heads/","")+"\n");
            messageChainBuilder.add("Pusher:"+webHookInfo.pusher.name+"\n");
            messageChainBuilder.add(webHookInfo.before.substring(0,7)+" -> "+webHookInfo.after.substring(0,7));
            messageChainBuilder.add("\nCommits:");
            for (GithubCommit commit : webHookInfo.commits) {
                messageChainBuilder.add("\n"+commit.message+" - "+commit.author.username);
            }
        }else {
            return;
        }
        for (Bot instance : Bot.getInstances()){
            send(messageChainBuilder.asMessageChain(),webHookInfo.repository.full_name,instance);
        }
    }
    public static void send(MessageChain messageChain,String full_name,Bot bot){
        try{
            for (Group group : getGroups(bot, full_name)) {
                group.sendMessage(messageChain);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private static List<Group> getGroups(Bot bot,String full_name) throws IOException {
        File config = new File("./github.json");
        Gson gson = new Gson();
        if (!config.exists()){
            GithubSaveInfo githubSaveInfo = new GithubSaveInfo();
            FileUtil.writeFile(config,gson.toJson(githubSaveInfo).getBytes(StandardCharsets.UTF_8));
        }
        GithubSaveInfo githubSaveInfo;
        githubSaveInfo = gson.fromJson(new String(FileUtil.readFile(config)),GithubSaveInfo.class);
        ArrayList<Group> groups = new ArrayList<Group>();
        for (GithubSubscribeInfo subscribe : githubSaveInfo.getSubscribes()) {
            for (String repo : subscribe.getRepos()) {
                System.out.println("repo");
                if (repo.equals(full_name)){
                    System.out.println("true");
                    groups.add(bot.getGroup(subscribe.getGroup()));
                }
            }
        }
        return groups;
    }
    private MessageChain removeSub(Group group,String repo){
        try{
            if (!repo.contains("/")){
                MessageChainBuilder messageChainBuilder = new MessageChainBuilder();
                messageChainBuilder.add("Please use full name eg. CanYingisme-Git/ShadowQwQ_NextGen");
                return messageChainBuilder.asMessageChain();
            }
            File config = new File("./github.json");
            Gson gson = new Gson();
            if (!config.exists()){
                GithubSaveInfo githubSaveInfo = new GithubSaveInfo();
                FileUtil.writeFile(config,gson.toJson(githubSaveInfo).getBytes(StandardCharsets.UTF_8));
            }
            GithubSaveInfo githubSaveInfo;
            githubSaveInfo = gson.fromJson(new String(FileUtil.readFile(config)),GithubSaveInfo.class);
            for (GithubSubscribeInfo subscribe : githubSaveInfo.getSubscribes()) {
                if (subscribe.getGroup() == group.getId()){
                    subscribe.getRepos().removeIf(subscribeRepo -> subscribeRepo.equals(repo));
                }
            }
            FileUtil.writeFile(config,gson.toJson(githubSaveInfo).getBytes(StandardCharsets.UTF_8));
            MessageChainBuilder messageChainBuilder = new MessageChainBuilder();
            messageChainBuilder.add("Successfully remove WebHook");
            return messageChainBuilder.asMessageChain();
        }catch (Exception e){
            e.printStackTrace();
            MessageChainBuilder messageChainBuilder = new MessageChainBuilder();
            messageChainBuilder.add(e.getMessage());
            return messageChainBuilder.asMessageChain();
        }
    }
    private MessageChain addSub(Group group,String repo){
        try{
            if (!repo.contains("/")){
                MessageChainBuilder messageChainBuilder = new MessageChainBuilder();
                messageChainBuilder.add("Please use full name eg. CanYingisme-Git/ShadowQwQ_NextGen");
                return messageChainBuilder.asMessageChain();
            }
            File config = new File("./github.json");
            Gson gson = new Gson();
            if (!config.exists()){
                GithubSaveInfo githubSaveInfo = new GithubSaveInfo();
                FileUtil.writeFile(config,gson.toJson(githubSaveInfo).getBytes(StandardCharsets.UTF_8));
            }
            GithubSaveInfo githubSaveInfo;
            githubSaveInfo = gson.fromJson(new String(FileUtil.readFile(config)),GithubSaveInfo.class);
            for (GithubSubscribeInfo subscribe : githubSaveInfo.getSubscribes()) {
                if (subscribe.getGroup() == group.getId()){
                    for (String subscribeRepo : subscribe.getRepos()) {
                        if (subscribeRepo.equals(repo)){
                            MessageChainBuilder messageChainBuilder = new MessageChainBuilder();
                            messageChainBuilder.add("This repository has been added");
                            return messageChainBuilder.asMessageChain();
                        }
                    }
                    subscribe.addRepo(repo);
                    FileUtil.writeFile(config,gson.toJson(githubSaveInfo).getBytes(StandardCharsets.UTF_8));
                    MessageChainBuilder messageChainBuilder = new MessageChainBuilder();
                    messageChainBuilder.add("Successfully add WebHook");
                    return messageChainBuilder.asMessageChain();
                }
            }
            GithubSubscribeInfo subscribe = new GithubSubscribeInfo(group.getId(),new ArrayList<String>());
            subscribe.addRepo(repo);
            githubSaveInfo.addSub(subscribe);
            FileUtil.writeFile(config,gson.toJson(githubSaveInfo).getBytes(StandardCharsets.UTF_8));
            MessageChainBuilder messageChainBuilder = new MessageChainBuilder();
            messageChainBuilder.add("Successfully add WebHook");
            return messageChainBuilder.asMessageChain();
        }catch (Exception e){
            e.printStackTrace();
            MessageChainBuilder messageChainBuilder = new MessageChainBuilder();
            messageChainBuilder.add(e.getMessage());
            return messageChainBuilder.asMessageChain();
        }
    }
}
