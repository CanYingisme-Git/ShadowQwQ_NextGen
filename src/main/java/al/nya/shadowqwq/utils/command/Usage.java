package al.nya.shadowqwq.utils.command;

import java.util.List;

public class Usage {
    private String name;
    private List<CommandArg> args;
    private String desc;
    private Side side;
    public Usage(String name, List<CommandArg> args,Side side){
        this.name = name;
        this.args = args;
        this.desc = "null";
        this.side = side;
    }
    public Usage(String name, List<CommandArg> args,String desc,Side side){
        this.name = name;
        this.args = args;
        this.desc = desc;
        this.side = side;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public Side getSide() {
        return side;
    }

    public List<CommandArg> getArgs() {
        return args;
    }
}
