package al.nya.shadowqwq.utils.json.github;

import java.util.ArrayList;
import java.util.List;

public class GithubSaveInfo {
    private List<GithubSubscribeInfo> subscribes;
    public GithubSaveInfo(){
        subscribes = new ArrayList<GithubSubscribeInfo>();
    }
    public void addSub(GithubSubscribeInfo subscribeInfo){
        subscribes.add(subscribeInfo);
    }

    public List<GithubSubscribeInfo> getSubscribes() {
        return subscribes;
    }

    public void setSubscribes(List<GithubSubscribeInfo> subscribes) {
        this.subscribes = subscribes;
    }
}
