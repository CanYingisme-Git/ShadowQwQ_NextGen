package al.nya.shadowqwq.modules;

import al.nya.shadowqwq.annotation.Command;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;

@Command(prefix = "github")
public class GithubWebhook extends Module {
    public GithubWebhook(String name) {
        super(name);
    }

}
