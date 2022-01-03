package al.nya.shadowqwq.utils.event;

import net.mamoe.mirai.event.Event;

public class EventProcessor {
    private Class<?> event;
    private String method;
    public EventProcessor(Class<?> event,String method){
        this.event = event;
        this.method = method;
    }

    public Class<?> getEvent() {
        return event;
    }

    public String getMethod() {
        return method;
    }
}
