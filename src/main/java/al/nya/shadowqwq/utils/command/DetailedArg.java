package al.nya.shadowqwq.utils.command;

public class DetailedArg extends CommandArg{
    private Class clazz;
    public DetailedArg(Class clazz,String desc){
        super(desc);
        this.clazz = clazz;
    }

    public Class getClazz() {
        return clazz;
    }
}
