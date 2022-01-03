package al.nya.shadowqwq.utils.command;

import al.nya.shadowqwq.ShadowQwQ;
import al.nya.shadowqwq.modules.Module;
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
                List<Class> classes = parseArgs(messages);
                int index = 0;
                for (DetailedArg arg : usage.getArgs()) {
                    if (!strict){
                        if (arg.getClazz() == Double.class){
                            if (arg.getClazz() != Double.class && arg.getClazz() != Integer.class){
                                return false;
                            }
                        }
                    }else {
                        if (arg.getClazz() != classes.get(index)){
                            return false;
                        }
                    }
                    index += 1;
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
        for (DetailedArg arg : usage.getArgs()) {
            Class clazz = arg.getClazz();
            if (clazz == Integer.class){
                objects.add(Integer.parseInt(messages[2+index]));
            }else if (clazz == String.class){
                objects.add(messages[2+index]);
            }else if (clazz == Double.class){
                objects.add(Double.parseDouble(messages[2+index]));
            }
            index += 1;
        }
        return objects;
    }
    private static int getLength(Usage usage){
        return 2 + usage.getArgs().size();
    }
    private static List<Class> parseArgs(String[] messages){
        List<String> strings = Arrays.asList(messages);
        List<Class> classes = new ArrayList<Class>();
        int index = 0;
        for (String string : strings) {
            if (index > 1){
                try {
                    Integer.parseInt(string);
                    classes.add(Integer.class);
                }catch (Exception e){
                    try{
                        Double.parseDouble(string);
                        classes.add(Double.class);
                    }catch (Exception ex){
                        classes.add(String.class);
                    }
                }
            }
            index += 1;
        }
        return classes;
    }
}
