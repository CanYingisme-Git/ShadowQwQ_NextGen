package al.nya.shadowqwq.annotation;

import al.nya.shadowqwq.utils.command.Side;
import al.nya.shadowqwq.utils.command.Usage;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {
    String prefix();
}
