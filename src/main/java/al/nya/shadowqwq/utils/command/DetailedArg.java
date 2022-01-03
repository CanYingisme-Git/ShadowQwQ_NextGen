package al.nya.shadowqwq.utils.command;

public class DetailedArg {
    private Class clazz;
    private String desc;
    public DetailedArg(Class clazz,String desc){
        this.clazz = clazz;
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public Class getClazz() {
        return clazz;
    }
}
