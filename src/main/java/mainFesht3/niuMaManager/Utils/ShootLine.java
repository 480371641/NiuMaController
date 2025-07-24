package mainFesht3.niuMaManager.Utils;

import jdk.jshell.execution.LoaderDelegate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import org.bukkit.util.Vector;

import java.nio.Buffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ShootLine {
//
//    function crossPos(pos,d,direction)
//    local yaw = math.rad(direction.yaw+90)
//    local pitch = math.rad(-direction.pitch)
//
//    local ny = pos.y + d*math.sin(pitch)
//    local r = d*math.cos(pitch)
//    local nx = pos.x + r*math.cos(yaw)
//    local nz = pos.z + r*math.sin(yaw)
//            -- local allp = mc.getOnlinePlayers()
//            -- for y = 1,#allp do
//            --     local p = allp[y]
//            --     p:tell(ny.."\n"..d,5)
//    -- end
//    return FloatPos(nx,ny,nz,pos.dimid)
//    end
//
//    function phasePos(pos,d,direction,x,y,z)
//    x 左右 向右为正
//    y 上下 向上为正
//    z 前后 向前为正
//    local yaw = math.rad(direction.yaw+90+90) --再增加90度，向量位于玩家右侧方向
//    local pitch = math.rad(-direction.pitch+90)--再增加90度，向量位于玩家上侧方向
//
//    local ny = pos.y + y*math.sin(pitch)
//    local r = d*math.cos(pitch)
//    local nx = pos.x + x*math.cos(yaw)
//    local nz = pos.z + z*math.sin(yaw)
//    return FloatPos(nx,ny,nz,pos.dimid)
//    end
//
//
//    function getShootLine(startPos , endPos , num)
//    local d = getDistence(startPos,endPos)
//    local piece = d/num
//    local sinxz = (endPos.z-startPos.z)/d
//    local cosxz = (endPos.x-startPos.x)/d
//    local siny = (endPos.y-startPos.y)/d
//
//    local posList = {}
//    for i = 0,num+1 do
//    local x = startPos.x + piece*i*cosxz
//    local z= startPos.z + piece*i*sinxz
//
//    local y = startPos.y + piece*i*siny
//
//    posList[#posList+1] = FloatPos(x,y,z,startPos.dimid)
//    end
//    return posList
//            end

//    Player player;
    Location playerPos;

    public ShootLine(Location playerPos){
        this.playerPos = playerPos;
    }

    /**
     * 根据玩家朝向计算偏移位置
     * x：左右偏移（向右为正）
     * y：上下偏移（向上为正）
     * z：前后偏移（向前为正）
     * @field  playerPos 玩家当前位置
     * @param x 左右偏移量
     * @param y 上下偏移量
     * @param z 前后偏移量
     * @return 计算后的目标位置
     */
    public Location getPhasePos( double x, double y, double z) {
        // 计算水平旋转角（yaw），增加180 使向量指向玩家右侧方向，转换为弧度
//        Bukkit.getLogger().info(playerPos.getPitch()+"");
        double yaw =  Math.toRadians(playerPos.getYaw()+180);
        // 计算垂直旋转角（pitch），增加90度使向量指向玩家上侧方向，转换为弧度
        double pitch = Math.toRadians(-playerPos.getPitch()+90);

        double raw_yaw = Math.toRadians(playerPos.getYaw() + 90);
        double raw_pitch = Math.toRadians(-playerPos.getPitch());


        double r = y * Math.cos(pitch);
        double rstright = z * Math.cos( raw_pitch );
        // 计算Y轴（上下）偏移
        double ny = playerPos.getY() + y * Math.sin(pitch) + z * Math.sin(raw_pitch) ;
        // 计算X轴（左右）偏移
        double nx = playerPos.getX() + (x + r ) * Math.cos(yaw) + rstright * Math.cos(raw_yaw);
        // 计算Z轴（前后）偏移
        double nz = playerPos.getZ() + (x + r ) * Math.sin(yaw) + rstright * Math.sin(raw_yaw);

        // 返回包含世界信息的位置对象
        return new Location(playerPos.getWorld(), nx, ny, nz);
    }


//    public Location getPhasePos(double distance , double x , double y , double z){
//        //    x 左右 向右为正
//        //    y 上下 向上为正
//        //    z 前后 向前为正
//
//    }

    public Location getCenterPos(double distance){
        Location newpos ;
        Vector direction_Vector = playerPos.getDirection().multiply(distance);
        return new Location(playerPos.getWorld() ,
                playerPos.getX() + direction_Vector.getX() ,
                playerPos.getY() + direction_Vector.getY() ,
                playerPos.getZ() + direction_Vector.getZ()
        );
    }

    /**
     * 生成从起点到终点的等距采样点列表（用于射线检测等场景）
     * @param startPos 起点位置
     * @param endPos 终点位置
     * @param num 采样点数量（不含起点和终点）
     * @return 包含所有采样点的列表
     */
    public List<Location> getShootLine(Location startPos, Location endPos, int num) {
        // 计算总距离
        double d = startPos.distance(endPos);

        // 计算每个分段的长度
        double piece = d / num;

        // 计算方向余弦（各轴上的单位方向向量分量）
        double sinxz = (endPos.getZ() - startPos.getZ()) / d;
        double cosxz = (endPos.getX() - startPos.getX()) / d;
        double siny = (endPos.getY() - startPos.getY()) / d;

        // 初始化结果列表
        List<Location> posList = new ArrayList<>();

        // 生成采样点（包括起点前一个点和终点后一个点）
        for (int i = 0; i <= num + 1; i++) {
            double x = startPos.getX() + piece * i * cosxz;
            double z = startPos.getZ() + piece * i * sinxz;
            double y = startPos.getY() + piece * i * siny;

            // 创建包含相同世界信息的位置
            posList.add(new Location(startPos.getWorld(), x, y, z));
        }

        return posList;
    }


}
