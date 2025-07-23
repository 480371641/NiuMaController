package mainFesht3.niuMaManager.Utils;

import jdk.jshell.execution.LoaderDelegate;

import javax.xml.stream.Location;

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


    Location playerPos;

    public ShootLine(Location playerPos){
        this.playerPos = playerPos;
    }



//    public Location getCenterPos(double distance){
//        Location newpos ;
////        function crossPos(pos,d,direction)
////        local yaw = math.rad(direction.yaw+90)
////        local pitch = math.rad(-direction.pitch)
////
////        local ny = pos.y + d*math.sin(pitch)
////        local r = d*math.cos(pitch)
////        local nx = pos.x + r*math.cos(yaw)
////        local nz = pos.z + r*math.sin(yaw)
////                -- local allp = mc.getOnlinePlayers()
////                -- for y = 1,#allp do
////                --     local p = allp[y]
////                --     p:tell(ny.."\n"..d,5)
////        -- end
////        return FloatPos(nx,ny,nz,pos.dimid)
////        end
//
//
//        return newpos;
//    }



}
