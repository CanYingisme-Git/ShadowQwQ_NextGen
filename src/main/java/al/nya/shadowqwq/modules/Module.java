package al.nya.shadowqwq.modules;

import al.nya.shadowqwq.annotation.Command;
import al.nya.shadowqwq.utils.command.Usage;
import al.nya.shadowqwq.utils.event.EventProcessor;
import net.mamoe.mirai.event.Event;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public abstract class Module {
    private String name;
    private ArrayList<Usage> usages = new ArrayList<Usage>();
    private List<EventProcessor> events= new ArrayList<EventProcessor>();
    public Module(String name){
        this.name = name;
    }
    public void registerEvent(EventProcessor eventProcessor){
        events.add(eventProcessor);
    }

    public ArrayList<Usage> getUsages() {
        return usages;
    }
    public boolean hasCommand(){
        for (Annotation annotation : this.getClass().getAnnotations()) {
            if (annotation.annotationType() == Command.class)return true;
        }
        return false;
    }
    public String getCommand(){
        for (Annotation annotation : this.getClass().getAnnotations()) {
            if (annotation instanceof Command){
                return ((Command) annotation).prefix();
            }
        }
        return null;
    }
    public String getName() {
        return name;
    }
    public void addUsage(Usage usage){
        usages.add(usage);
    }
}
