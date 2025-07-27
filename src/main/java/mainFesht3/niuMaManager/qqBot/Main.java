package mainFesht3.niuMaManager.qqBot;

import java.io.IOException;


import mainFesht3.niuMaManager.NiuMaManager;

import org.bukkit.Bukkit;
import org.bukkit.Server;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.*;
/**
 * 基于Java-WebSocket库的WebSocket服务器
 * 支持简单消息处理和多客户端管理
 */
public class Main extends WebSocketServer {
    private
    final JavaPlugin plugin;
    private final ConcurrentHashMap<String, WebSocket> clients = new ConcurrentHashMap<>();

    Gson gs = new Gson();

    public Main(JavaPlugin plugin, int port) {
        super(new InetSocketAddress(port));
        this.plugin = plugin;
    }

    private Map<String , Object> createApiPacket(String api_name , Map<String , Object> params){
        Map<String, Object> model = new ConcurrentHashMap<>();
        model.put("action" , api_name);
        model.put("params" , params);
        model.put("echo" , "NiuMaManagerTEST");
        return model;
    }

    private void sendGroupMessage(long group_id , String msg){
        Map<String , Object> msg_s = new ConcurrentHashMap<>();
        msg_s.put("message",msg);
        msg_s.put("group_id" , group_id);
        Map<String , Object> request = createApiPacket("send_group_msg" , msg_s);
//        plugin.getLogger().info("数据处理成功，准备返回: "+gs.toJson(request));
        broadcastStr(gs.toJson(request));
    }
    private void sendPrivateMessage(long qq , String msg){
        Map<String , Object> msg_s = new ConcurrentHashMap<>();
        msg_s.put("message",msg);
        msg_s.put("user_id" , qq);
        Map<String , Object> request = createApiPacket("send_private_msg" , msg_s);
//        plugin.getLogger().info("数据处理成功，准备返回: "+gs.toJson(request));
        broadcastStr(gs.toJson(request));
    }


    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        String clientId = conn.getRemoteSocketAddress().toString();
        clients.put(clientId, conn);
        plugin.getLogger().info("新客户端连接: " + clientId);

        // 向客户端发送欢迎消息
//        conn.send("欢迎连接到服务器!");
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        String clientId = conn.getRemoteSocketAddress().toString();
        clients.remove(clientId);
        plugin.getLogger().info("客户端断开连接: " + clientId);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        try{
            String clientId = conn.getRemoteSocketAddress().toString();
//            plugin.getLogger().info("收到消息 [" + clientId + "]: " + message);
            JsonObject obj = gs.fromJson(message , JsonObject.class);
            if(obj.has("post_type")){
                if(Objects.equals(obj.get("post_type").getAsString(), "message")) {
                    //message process
                    String raw_msg = obj.get("raw_message").getAsString();
                    switch (raw_msg) {
                        case "在线":
                                Collection<? extends Player> olp = Bukkit.getOnlinePlayers();
                                String lname = "";
                                for(Player p : olp){
                                    lname += "\n";
                                    if(NiuMaManager.isVIP(p)){
                                        lname += "<VIP>";
                                    }
                                    lname+= p.getName();
                                }

                                sendMessage("当前在线人数：" + olp.size()+lname , obj);
                            break;
                        case "tps":
                            double tick = 1/NiuMaManager.getTickLate();
                            sendMessage( "Server Instantaneous TPS :" + (Math.round(tick*10)/10) +"\nAccording to tick delay." ,obj);
                            break;


                    }
                }
            }
        }catch(Exception e){
            plugin.getLogger().info("遇到了一些问题，在解析"+message);
        }

    }
//        // 在主线程执行Bukkit操作（关键！）
//        Bukkit.getScheduler().runTask(plugin, () -> {
//            // 在这里可以安全地调用Bukkit API
//            // 例如发送消息给玩家、执行命令等
//            Bukkit.broadcastMessage("[WebSocket] " + clientId + ": " + message);
//        });
//
//        // 广播消息给所有客户端
//        broadcastMessage(clientId + ": " + message);


    @Override
    public void onError(WebSocket conn, Exception ex) {
        if (conn != null) {
            String clientId = conn.getRemoteSocketAddress().toString();
            plugin.getLogger().severe("客户端 " + clientId + " 发生错误: " + ex.getMessage());
        } else {
            plugin.getLogger().severe("服务器错误: " + ex.getMessage());
        }
        ex.printStackTrace();
    }

    @Override
    public void onStart() {
        plugin.getLogger().info("WebSocket服务器已启动，监听端口: " + getPort());
        setConnectionLostTimeout(100); // 设置连接超时时间
    }

    /**
     * 广播消息给所有客户端
     */
    public void broadcastStr(String message) {
        clients.values().forEach(client -> {
            if (client.isOpen()) {
                client.send(message);
            }
        });
//        plugin.getLogger().info("发送"+message+"成功！！！！！！！！！！");
    }

    /**
     * 向特定客户端发送消息
     */
    public void sendStr(String clientId, String message) {
        WebSocket client = clients.get(clientId);
        if (client != null && client.isOpen()) {
            client.send(message);
        }
    }

    /**
     *
     * @param msg 要发送的信息字符串
     * @param obj 附带整段的websocekt请求段，解析为jsonObject格式
     */
    public void sendMessage(String msg, JsonObject obj){
        if ( obj.get("message_type").getAsString().equals("group") ) {
            
            long group_id = obj.get("group_id").getAsLong();
            sendGroupMessage(group_id, msg);
        } else if (obj.get("message_type").getAsString().equals("private")) {

            long qq = obj.get("user_id").getAsLong();
            sendPrivateMessage(qq, msg);
        }
    }
}