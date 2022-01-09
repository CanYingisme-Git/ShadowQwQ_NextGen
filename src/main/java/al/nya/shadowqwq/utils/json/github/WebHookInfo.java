package al.nya.shadowqwq.utils.json.github;

import java.util.List;

public class WebHookInfo {
    public String zen;
    public String before;
    public String after;
    public GithubRepository repository;
    public GithubUser sender;
    public GithubPusher pusher;
    public String ref;
    public List<GithubCommit> commits;
}
