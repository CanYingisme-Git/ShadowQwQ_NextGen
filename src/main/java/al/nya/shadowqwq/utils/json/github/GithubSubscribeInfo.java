package al.nya.shadowqwq.utils.json.github;

import java.util.List;

public class GithubSubscribeInfo {
    private long group;
    private List<String> repo_name;
    public GithubSubscribeInfo(long group,List<String> repo_name){
        this.group = group;
        this.repo_name = repo_name;
    }
    public void addRepo(String s){
        repo_name.add(s);
    }

    public List<String> getRepos() {
        return repo_name;
    }

    public long getGroup() {
        return group;
    }
}
