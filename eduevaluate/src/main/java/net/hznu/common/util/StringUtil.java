package net.hznu.common.util;

import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.mvc.Mvcs;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.servlet.http.HttpServletRequest;
import java.text.NumberFormat;
import java.util.Random;
import java.util.regex.Pattern;

/**
 * Created by Wizzer.cn on 2015/7/4.
 */
@IocBean
public class StringUtil {

    private static final Pattern IPV4_PATTERN =
            Pattern.compile(
                    "^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$");

    private static final Pattern IPV6_STD_PATTERN =
            Pattern.compile(
                    "^(?:[0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$");

    private static final Pattern IPV6_HEX_COMPRESSED_PATTERN =
            Pattern.compile(
                    "^((?:[0-9A-Fa-f]{1,4}(?::[0-9A-Fa-f]{1,4})*)?)::((?:[0-9A-Fa-f]{1,4}(?::[0-9A-Fa-f]{1,4})*)?)$");

    public static boolean isIPv4Address(final String input) {
        return IPV4_PATTERN.matcher(input).matches();
    }

    public static boolean isIPv6StdAddress(final String input) {
        return IPV6_STD_PATTERN.matcher(input).matches();
    }

    public static boolean isIPv6HexCompressedAddress(final String input) {
        return IPV6_HEX_COMPRESSED_PATTERN.matcher(input).matches();
    }

    public static boolean isIPv6Address(final String input) {
        return isIPv6StdAddress(input) || isIPv6HexCompressedAddress(input);
    }

    /**
     * 计算MD5密码
     *
     * @param loginname
     * @param password
     * @param createAt
     * @return
     */
    public static String getPassword(String loginname, String password, int createAt) {
        String p = Lang.md5(Lang.md5(password) + loginname + createAt);
        return 'w' + p.substring(0, p.length() - 1);
    }

    /**
     * 获得用户远程地址
     */
    public static String getRemoteAddr() {
        HttpServletRequest request = Mvcs.getReq();
        String remoteAddr = request.getHeader("X-Real-IP");
        if (Strings.isNotBlank(remoteAddr)) {
            remoteAddr = request.getHeader("X-Forwarded-For");
        } else if (Strings.isNotBlank(remoteAddr)) {
            remoteAddr = request.getHeader("Proxy-Client-IP");
        } else if (Strings.isNotBlank(remoteAddr)) {
            remoteAddr = request.getHeader("WL-Proxy-Client-IP");
        }
        String ip = remoteAddr != null ? remoteAddr : Strings.sNull(request.getRemoteAddr());
        if (isIPv4Address(ip) || isIPv6Address(ip)) {
            return ip;
        }
        return "";
    }

    /**
     * 去掉URL中?后的路径
     *
     * @param p
     * @return
     */
    public static String getPath(String p) {
        if (Strings.sNull(p).contains("?")) {
            return p.substring(0, p.indexOf("?"));
        }
        return Strings.sNull(p);
    }

    /**
     * 获得父节点ID
     *
     * @param s
     * @return
     */
    public static String getParentId(String s) {
        if (!Strings.isEmpty(s) && s.length() > 4) {
            return s.substring(0, s.length() - 4);
        }
        return "";
    }

    /**
     * 得到n位随机数
     *
     * @param s
     * @return
     */
    public static String getRndNumber(int s) {
        Random ra = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s; i++) {
            sb.append(String.valueOf(ra.nextInt(8)));
        }
        return sb.toString();
    }

    /**
     * 判断是否以字符串开头
     *
     * @param str
     * @param s
     * @return
     */
    public boolean startWith(String str, String s) {
        return Strings.sNull(str).startsWith(Strings.sNull(s));
    }

    /**
     * 判断是否包含字符串
     *
     * @param str
     * @param s
     * @return
     */
    public boolean contains(String str, String s) {
        return Strings.sNull(str).contains(Strings.sNull(s));
    }

    /**
     *
     * @param value 需要判断的指标的分数（传入前已经是数组）元素个数与条件分割后个数一致
     * @param condition 判断公式（方法中用split变成数组）；代表and ，代表or
     * @return
     */
    public static Integer calculateExpression(String[] value,String condition)  {
        ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        ScriptEngine scriptEngine = scriptEngineManager.getEngineByName("JavaScript");
        String expression = "";
        String[] target = condition.split(";");
        try {
            int indexOfValue=0;
            for(int i =0;i<target.length;i++){
                String[] target_l = target[i].split(",");
                expression += "(";
                for(int j=0;j<target_l.length;j++){
                    if(value[indexOfValue].indexOf("%")!=-1){
                        NumberFormat nf= NumberFormat.getPercentInstance();
                        Number m=nf.parse(value[indexOfValue]);
                        expression += m + target_l[j] +"||";
                    }else
                        expression += value[indexOfValue] + target_l[j] +"||";
                    //完成一次值和条件的逻辑表达式后，开始下一个值的比较
                    indexOfValue ++;
                }
                expression = expression.substring(0,expression.length()-2);
                expression +=")&&";
            }
            expression = expression.substring(0,expression.length()-2);
            boolean result = (boolean) scriptEngine.eval(expression);
            //boolean result = (boolean) scriptEngine.eval(expression);
            if (result == true) {
                return 1;
            } else{
                return 0;
            }
        } catch (Exception e) {
            return -1;
        }
    }
}

