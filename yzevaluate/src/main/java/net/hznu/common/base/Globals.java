package net.hznu.common.base;

import net.hznu.modules.models.sys.Sys_config;
import net.hznu.modules.models.sys.Sys_route;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wizzer on 2016/6/22.
 */
public class Globals {
    //项目路径
    public static String AppRoot = "";
    //项目目录
    public static String AppBase = "";
    //项目名称
    public static String AppName = "NutzWk 开发框架";
    //项目短名称
    public static String AppShrotName = "NutzWk";
    //项目域名
    public static String AppDomain = "127.0.0.1";
    //文件上传路径
    public static String AppUploadPath = "/upload";
    //自评截止日期
    public static Date SelfDeadline=null;
    //评估学年
    public static int EvaluateYear=2018;
    //系统自定义参数
    public static Map<String, String> MyConfig;
    //自定义路由
    public static Map<String, Sys_route> RouteMap;

    public static void init(Dao dao)  {
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy年MM月dd日 hh:mm:ss");
        if (Globals.MyConfig == null) {
            Globals.MyConfig = new HashMap<>();
        } else Globals.MyConfig.clear();
        List<Sys_config> configList = dao.query(Sys_config.class, Cnd.NEW());
        for (Sys_config sysConfig : configList) {
            switch (sysConfig.getConfigKey()) {
                case "AppName":
                    Globals.AppName = sysConfig.getConfigValue();
                    break;
                case "AppShrotName":
                    Globals.AppShrotName = sysConfig.getConfigValue();
                    break;
                case "AppDomain":
                    Globals.AppDomain = sysConfig.getConfigValue();
                    break;
                case "AppUploadPath":
                    Globals.AppUploadPath = sysConfig.getConfigValue();
                    break;
                case "Selfdeadline":
                    try {
                        String deadline = sysConfig.getConfigValue()+" 23:59:59";
                        Globals.SelfDeadline=  simpleDateFormat.parse(deadline);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                default:
                    Globals.MyConfig.put(sysConfig.getConfigKey(), sysConfig.getConfigValue());
                    break;
            }
        }
    }

    public static void initRoute(Dao dao) {
        if (Globals.RouteMap == null) {
            Globals.RouteMap = new HashMap<>();
        } else Globals.RouteMap.clear();
        List<Sys_route> routeList = dao.query(Sys_route.class, Cnd.where("disabled", "=", false));
        for (Sys_route route : routeList) {
            RouteMap.put(route.getUrl(), route);
        }
    }
}
