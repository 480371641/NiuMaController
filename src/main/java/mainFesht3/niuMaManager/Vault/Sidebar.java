package mainFesht3.niuMaManager.Vault;

import mainFesht3.niuMaManager.NiuMaManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.*;

import static org.bukkit.Bukkit.getServer;

//失败的侧边栏，弃用
//弃用
//弃用
//弃用
//弃用
//弃用

public class Sidebar {
    Economy econ = NiuMaManager.getEconomy();

    // 存储每个玩家的计分板和团队信息
    private final Map<UUID, Scoreboard> playerScoreboards = new HashMap<>();
    private final Map<UUID, List<Team>> playerTeams = new HashMap<>();

    // 侧边栏行数
    private static final int MAX_LINES = 15;

    // 创建颜色代码数组作为条目的唯一标识符
    private static final String[] COLOR_CODES = new String[MAX_LINES];
    static {
        ChatColor[] colors = ChatColor.values();
        for (int i = 0; i < MAX_LINES; i++) {
            COLOR_CODES[i] = colors[i].toString();
        }
    }

    public void updateEveryOnlinePlayerSidebar() {
        Collection<? extends Player> olp = Bukkit.getOnlinePlayers();
        for (Player player : olp) {
            updateSidebar(player);
        }
    }

    // 创建侧边栏
    private void createSidebar(Player player) {
        ScoreboardManager manager = getServer().getScoreboardManager();
        Scoreboard scoreboard = manager.getNewScoreboard();

        // 创建侧边栏目标
        Objective objective = scoreboard.registerNewObjective(
                "sidebar",
                "dummy",
                ChatColor.translateAlternateColorCodes('&', "&a&lNiuMa Server")
        );
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        // 初始化团队
        List<Team> teams = initializeTeams(scoreboard);

        // 保存玩家的计分板和团队信息
        playerScoreboards.put(player.getUniqueId(), scoreboard);
        playerTeams.put(player.getUniqueId(), teams);

        // 应用到玩家
        player.setScoreboard(scoreboard);
    }

    // 初始化团队
    private List<Team> initializeTeams(Scoreboard scoreboard) {
        List<Team> teams = new ArrayList<>();

        for (int i = 0; i < MAX_LINES; i++) {
            // 创建团队，使用行号作为团队名称
            Team team = scoreboard.registerNewTeam("line_" + i);

            // 添加条目（使用颜色代码作为唯一标识符）
            String entry = COLOR_CODES[i];
            team.addEntry(entry);

            // 设置条目分数（控制显示顺序）
            scoreboard.getObjective("sidebar").getScore(entry).setScore(MAX_LINES - i);

            // 初始化为空文本
            team.setPrefix("");
            team.setSuffix("");

            teams.add(team);
        }

        return teams;
    }

    // 更新侧边栏
    private void updateSidebar(Player player) {
        UUID playerId = player.getUniqueId();
        Scoreboard scoreboard = playerScoreboards.get(playerId);

        // 如果计分板不存在，创建它
        if (scoreboard == null) {
            createSidebar(player);
            scoreboard = playerScoreboards.get(playerId);
        }

        // 获取团队列表
        List<Team> teams = playerTeams.get(playerId);
        if (teams == null || teams.isEmpty()) {
            return;
        }

        // 生成侧边栏内容
        List<String> lines = generateSidebarLines(player);

        // 更新每一行
        for (int i = 0; i < Math.min(lines.size(), teams.size()); i++) {
            String line = lines.get(i);
            Team team = teams.get(i);

            // 更新团队的前缀和后缀
            updateTeamText(team, line);
        }
    }

    // 生成侧边栏内容
    private List<String> generateSidebarLines(Player player) {
        List<String> lines = new ArrayList<>();
        double nmscore = econ.getBalance(player);
        String nmscore_show = econ.format(nmscore);

        lines.add("§7§m------------");
        lines.add("§a玩家: §f" + player.getName());
        lines.add("§b在线: §f" + getServer().getOnlinePlayers().size());
        lines.add("§r牛马积分：" + nmscore_show);
        lines.add("§r牛马币：§g" + NiuMaManager.getPlayerNMB(player));
        lines.add("§b§lQQ群: 750315622 ");
        lines.add("§7§m------------");

        return lines;
    }

    // 更新团队文本（处理文本长度限制）
    private void updateTeamText(Team team, String text) {
        // 处理Minecraft计分板文本长度限制（prefix + entry + suffix ≤ 40字符）
        // 通常entry使用1个字符，所以prefix + suffix ≤ 39

        // 如果文本长度小于等于16，全部放入prefix
        if (text.length() <= 16) {
            team.setPrefix(text);
            team.setSuffix("");
            return;
        }

        // 如果文本长度大于16，分割为prefix和suffix
        String prefix = text.substring(0, 16);
        String suffix = text.substring(16);

        // 如果suffix仍然太长，截断
        if (suffix.length() > 16) {
            suffix = suffix.substring(0, 16);
        }

        team.setPrefix(prefix);
        team.setSuffix(suffix);
    }
}