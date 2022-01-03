package al.nya.shadowqwq.modules;

import al.nya.shadowqwq.ShadowQwQ;
import al.nya.shadowqwq.annotation.Command;
import al.nya.shadowqwq.annotation.EventTarget;
import al.nya.shadowqwq.utils.command.CommandUtil;
import al.nya.shadowqwq.utils.command.DetailedArg;
import al.nya.shadowqwq.utils.command.Side;
import al.nya.shadowqwq.utils.command.Usage;
import net.mamoe.mirai.event.events.GroupMessageEvent;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;

@Command(prefix = "Misc")
public class Misc extends Module{
    private Usage bmiUsage = new Usage("bmi", Arrays.asList(new DetailedArg(Double.class,"Height(CM)"),new DetailedArg(Double.class,"Weight(KG)")), Side.All);
    public Misc() {
        super("Mise");
        addUsage(bmiUsage);
    }
    @EventTarget
    public void onGroupMessage(GroupMessageEvent event){
        if (CommandUtil.isUsage(event.getMessage(),bmiUsage,this,true)){
            List<Object> objects = CommandUtil.fastParse(event.getMessage(),bmiUsage);
            ShadowQwQ.INSTANCE.logger.info("toggle");
            event.getGroup().sendMessage(bmi((Double)objects.get(0),(Double)objects.get(1)));
        }
    }


    private String bmi(double height,double weight){
        try{
            StringBuilder sb = new StringBuilder();
            height = height / 100;
            height *= height;
            double bmi = weight/height;
            double truncatedDouble = BigDecimal.valueOf(bmi)
                    .setScale(1, RoundingMode.HALF_UP)
                    .doubleValue();
            sb.append("你的BMI指数为").append(truncatedDouble);
            return sb.toString();
        }catch (Exception e){
            return e.getMessage();
        }
    }
}
