package al.nya.shadowqwq.utils.json.bot;

import net.mamoe.mirai.contact.Group;

import java.util.ArrayList;
import java.util.List;

public class BlackList {
    private List<Long> groups;
    public BlackList(){
        groups = new ArrayList<Long>();
    }
    public void add(Group group){
        groups.add(group.getId());
    }

    public List<Long> getGroups() {
        return groups;
    }
}
