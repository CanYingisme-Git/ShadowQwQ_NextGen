package al.nya.shadowqwq.utils.command;

import al.nya.shadowqwq.ShadowQwQ;
import al.nya.shadowqwq.modules.Analyze;
import al.nya.shadowqwq.modules.Module;
import al.nya.shadowqwq.modules.ModuleManager;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.OnlineMessageSource;
import net.mamoe.mirai.message.data.SingleMessage;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandUtil {
    public static boolean isUsage(MessageChain messageChain, Usage usage, Module module,boolean strict){
        StringBuilder margeMessage = new StringBuilder();
        for (SingleMessage singleMessage : messageChain) {
            if (!(singleMessage instanceof OnlineMessageSource)){
                margeMessage.append(singleMessage.toString());
            }
        }
        String message = margeMessage.toString();
        String[] messages = message.split(" ");
        if (messages.length == getLength(usage)){
            if (messages[0].equalsIgnoreCase("/"+module.getCommand())){
                if (messages[1].equalsIgnoreCase(usage.getName())){
                    List<String> strings = parseArgs(messages);
                    int index = 0;
                    for (CommandArg arg : usage.getArgs()) {
                        if (arg instanceof DetailedArg){
                            DetailedArg detailedArg = (DetailedArg) arg;
                            try{
                                if (detailedArg.getClazz() == Integer.class){
                                    Integer.parseInt(strings.get(index));
                                } else if (detailedArg.getClazz() == Double.class) {
                                    Double.parseDouble(strings.get(index));
                                }else if (detailedArg.getClazz() == Long.class){
                                    Long.parseLong(strings.get(index));
                                }else if (detailedArg.getClazz() == String.class){

                                }else {
                                    return false;
                                }
                            }catch (Exception e){
                                return false;
                            }
                        }else if (arg instanceof EnumArg){
                            EnumArg enumArg = (EnumArg) arg;
                            try{
                                Enum anEnum = null;
                                for (Enum enumArgEnum : enumArg.getEnums()) {
                                    if (strings.get(index).equalsIgnoreCase(enumArgEnum.name())){
                                        anEnum = enumArgEnum;
                                    }
                                }
                                if (anEnum == null)return false;
                            }catch (Exception e){
                                return false;
                            }
                        }
                        index += 1;
                    }
                }else {
                    return false;
                }
            }else {
                return false;
            }
            return true;
        }else {
            return false;
        }
    }
    public static List<Object> fastParse(MessageChain messageChain,Usage usage){
        StringBuilder margeMessage = new StringBuilder();
        for (SingleMessage singleMessage : messageChain) {
            if (!(singleMessage instanceof OnlineMessageSource)){
                margeMessage.append(singleMessage.toString());
            }
        }
        String message = margeMessage.toString();
        String[] messages = message.split(" ");
        List<Object> objects = new ArrayList<Object>();
        int index = 0;
        for (CommandArg arg : usage.getArgs()) {
            if (arg instanceof DetailedArg){
                Class clazz = ((DetailedArg) arg).getClazz();
                if (clazz == Integer.class){
                    objects.add(Integer.parseInt(messages[2+index]));
                }else if (clazz == String.class){
                    objects.add(messages[2+index]);
                }else if (clazz == Double.class){
                    objects.add(Double.parseDouble(messages[2+index]));
                }else if (clazz == Long.class){
                    objects.add(Long.parseLong(messages[2+index]));
                }
            }else if (arg instanceof EnumArg){
                for (Enum anEnum : ((EnumArg) arg).getEnums()) {
                    if (anEnum.name().equalsIgnoreCase(messages[2+index])){
                        objects.add(anEnum);
                        break;
                    }
                }
            }

            index += 1;
        }
        return objects;
    }
    private static int getLength(Usage usage){
        return 2 + usage.getArgs().size();
    }
    private static List<String> parseArgs(String[] messages){
        int index = 0;
        List<String> strings = new ArrayList<String>();
        for (String message : messages) {
            if (index > 1)strings.add(message);
            index += 1;
        }
        return strings;
    }
}
