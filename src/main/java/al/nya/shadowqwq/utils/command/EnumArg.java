package al.nya.shadowqwq.utils.command;

import java.util.List;

public class EnumArg extends CommandArg{
    private Enum[] enums;
    public EnumArg(Enum[] enums, String desc){
        super(desc);

        this.enums = enums;
    }
    public Enum[] getEnums() {
        return enums;
    }
}
