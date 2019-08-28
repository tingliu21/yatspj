package net.hznu.modules.controllers.platform.evaluate;

import net.hznu.common.annotation.SLog;
import net.hznu.common.base.Globals;
import net.hznu.common.base.Result;
import net.hznu.common.filter.PrivateFilter;
import net.hznu.common.page.DataTableColumn;
import net.hznu.common.page.DataTableOrder;
import net.hznu.modules.models.evaluate.EvaluateIndexTpl;
import net.hznu.modules.models.evaluate.Evaluate_index;
import net.hznu.modules.models.evaluate.Evaluate_records;
import net.hznu.modules.models.evaluate.Evaluate_special;
import net.hznu.modules.models.monitor.Monitor_index;
import net.hznu.modules.models.sys.Sys_unit;
import net.hznu.modules.models.sys.Sys_user;
import net.hznu.modules.services.evaluate.EvaluateIndexService;
import net.hznu.modules.services.evaluate.EvaluateRecordsService;
import net.hznu.modules.services.evaluate.EvaluateSpecialService;
import net.hznu.modules.services.evaluate.EvaluateindextplService;
import net.hznu.modules.services.monitor.MonitorIndexService;
import net.hznu.modules.services.sys.SysUnitService;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.nutz.dao.Cnd;
import org.nutz.dao.Sqls;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.lang.Files;
import org.nutz.lang.Strings;
import org.nutz.lang.random.R;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.adaptor.WhaleAdaptor;
import org.nutz.mvc.annotation.*;
import org.nutz.mvc.impl.AdaptorErrorContext;
import org.nutz.mvc.upload.TempFile;
import org.nutz.mvc.upload.UploadAdaptor;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.InputStream;
import java.util.*;

@IocBean
@At("/platform/evaluate/records")
@Filters({@By(type = PrivateFilter.class)})
public class EvaluateRecordsController {
    private static final Log log = Logs.get();
    @Inject
    private EvaluateRecordsService evaluateRecordsService;
    @Inject
    private SysUnitService unitService;
    @Inject
    private MonitorIndexService monitorIndexService;
    @Inject
    private EvaluateIndexService evaluateIndexService;
    @Inject
    private EvaluateindextplService evaluateindextplService;
    @Inject
    private SysUnitService sysUnitService;
    @Inject
    private EvaluateSpecialService evaluateSpecialService;


    @At("")
    @Ok("beetl:/platform/evaluate/records/index.html")
    @RequiresAuthentication
    public void index() {

    }
    @At("/special")
    @Ok("beetl:/platform/evaluate/records/specialindex.html")
    @RequiresAuthentication
    public void specindex( HttpServletRequest req) {

    }
    @At
    @Ok("json:full")
    @RequiresAuthentication
    public Object data(@Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {
        Cnd cnd = Cnd.NEW();
        return evaluateRecordsService.data(length, start, draw, order, columns, cnd, "unit");
    }
    //获取专家评审列表
    @At
    @Ok("json:full")
    @RequiresPermissions("evaluate.verify.special")
    public Object specdata(@Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {
        String sql = "SELECT a.* FROM evaluate_records a WHERE year= "+ Globals.EvaluateYear;

        //获取专家id
        Subject subject = SecurityUtils.getSubject();
        Sys_user user = (Sys_user) subject.getPrincipal();
        // 系统管理员可以审核所有的报告
        if(!subject.hasRole("sysadmin")) {
            //专家只审核自己分配的学校
            sql += " and a.id IN(SELECT b.evaluateid FROM evaluate_special b where b.specialid='"+user.getId()+ "')";

        }

        String s = sql;
        if (order != null && order.size() > 0) {
            for (DataTableOrder o : order) {
                DataTableColumn col = columns.get(o.getColumn());
                s += " order by a." + Sqls.escapeSqlFieldValue(col.getData()).toString() + " " + o.getDir();
            }
        }
        return evaluateRecordsService.data(length, start, draw, Sqls.create(sql), Sqls.create(s));
    }
    @At
    @Ok("beetl:/platform/evaluate/records/add.html")
    @RequiresAuthentication
    public void add(HttpServletRequest req) {
        List<Sys_unit> list = unitService.query(Cnd.orderBy().asc("path"));
        List<NutMap> units = new ArrayList<>();
        for (Sys_unit unit : list) {
            NutMap map = new NutMap();

            map.put("id", unit.getId());
            map.put("text", unit.getName());
            map.put("icon", Strings.sBlank(""));
            map.put("parent", "".equals(Strings.sNull(unit.getParentId())) ? "#" : unit.getParentId());
            map.put("data", unit.getUnitcode());
            if ((unit.getPath().length() >= 16 || !unit.isHasChildren())) {
                map.put("state", NutMap.NEW().addv("selected", true));
            } else {
                map.put("state", NutMap.NEW().addv("selected", false));
            }
            units.add(map);
        }
        req.setAttribute("units", Json.toJson(units));
    }

    @At
    @Ok("json")
    @SLog(tag = "新建Evaluate_records", msg = "")
    public Object addDo(@Param("unitIds") String unitIds, @Param("year") int year, HttpServletRequest req) {
        try {
            String[] ids = StringUtils.split(unitIds, ",");
            for (String s : ids) {
                if (!Strings.isEmpty(s)) {
                    Evaluate_records records = new Evaluate_records();
                    records.setYear(year);

                    records.setUnitID(s);


                    //插入评估记录
                    records = evaluateRecordsService.insert(records);
                    List<Monitor_index> monitorIndexs = monitorIndexService.query();

                    for (Monitor_index index : monitorIndexs) {
                        Evaluate_index evaluate = new Evaluate_index();
                        evaluate.setEvaluateId(records.getId());
                        evaluate.setIndexId(index.getId());
                        evaluate.setCode(index.getCode());

                        //插入监测指标记录
                        evaluateIndexService.insert(evaluate);
                    }
                }
            }
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }

    @At("/edit/?")
    @Ok("beetl:/platform/evaluate/records/edit.html")
    @RequiresAuthentication
    public Object edit(String id) {

        return evaluateRecordsService.fetch(id);
    }

    @At
    @Ok("json")
    @SLog(tag = "修改Evaluate_records", msg = "ID:${args[0].id}")
    public Object editDo(@Param("..") Evaluate_records evaluateRecords, HttpServletRequest req) {
        try {

            evaluateRecords.setOpAt((int) (System.currentTimeMillis() / 1000));
            evaluateRecordsService.updateIgnoreNull(evaluateRecords);
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }


    @At({"/delete", "/delete/?"})
    @Ok("json")
    @SLog(tag = "删除Evaluate_records", msg = "ID:${args[2].getAttribute('id')}")
    public Object delete(String id, @Param("ids") String[] ids, HttpServletRequest req) {
        try {
            if (ids != null && ids.length > 0) {
                evaluateRecordsService.delete(ids);
                req.setAttribute("id", org.apache.shiro.util.StringUtils.toString(ids));
            } else {
                evaluateRecordsService.delete(id);
                req.setAttribute("id", id);
            }
            return Result.success("system.success");
        } catch (Exception e) {
            return Result.error("system.error");
        }
    }


    @At("/detail/?")
    @Ok("beetl:/platform/evaluate/records/detail.html")
    @RequiresAuthentication
    public Object detail(String id) {
        if (!Strings.isBlank(id)) {
            return evaluateRecordsService.fetch(id);

        }
        return null;
    }
    //上传评估结果
    @At
    @Ok("beetl:/platform/evaluate/records/upload.html")
    @RequiresAuthentication
    public void upload() {}
    //临时用来导入山娟计算的数据
    @AdaptBy(type = UploadAdaptor.class, args = {"ioc:fileUpload"})
    @POST
    @At
    @Ok("json")
    @RequiresAuthentication
    //AdaptorErrorContext必须是最后一个参数
    public Object xlsfile(@Param("Filedata") TempFile tf, @Param("year") int year,@Param("bscore") boolean bscore,HttpServletRequest req, AdaptorErrorContext err) {
        try {
            if (err != null && err.getAdaptorErr() != null) {
                return NutMap.NEW().addv("code", 1).addv("msg", "文件不合法");
            } else if (tf == null) {
                return Result.error("空文件");
            } else {
                //导入数据库
                InputStream is = tf.getInputStream();
                evaluateRecordsService.excel2db(is, year,bscore);
                if(bscore) {
                   //导入分值时，生成评语
                    evaluateSpecialService.clear();
                   List<Evaluate_records> recordsList = evaluateRecordsService.query(Cnd.where("year", "=", year));
                   for (Evaluate_records record : recordsList) {
                       evaluateIndexService.generateRemark(record);
                   }
                }
                //这里已经通过数据库把数据导入到evaluate_value_temp_2018，暂时先将数据库的记录读到evaluate_index
                //importIndexValue();

                return Result.success("上传成功", tf.getSubmittedFileName());
            }
        } catch (Exception e) {
            return Result.error("系统错误");
        } catch (Throwable e) {
            return Result.error("文件格式错误");
        }
    }

    public void importIndexValue() {
        List<EvaluateIndexTpl> evaluatelist = evaluateindextplService.query();
        for (EvaluateIndexTpl tpl : evaluatelist) {

            //获取行政区划对应的单位
            Sys_unit unit = sysUnitService.fetch(Cnd.where("unitcode", "=", tpl.getXzqh()));
            //生成评估记录
            Evaluate_records records = evaluateRecordsService.fetch(Cnd.where("unitid", "=", unit.getId()).and("year", "=", tpl.getYear()));

            if (records == null) {
                records = new Evaluate_records();
                records.setYear(tpl.getYear());
                records.setUnitID(unit.getId());
                records = evaluateRecordsService.insert(records);
            }
            //插入指标
            Evaluate_index index = new Evaluate_index();
            index.setEvaluateId(records.getId());
            index.setCode("0101");
            index.setScore(tpl.getS0101());
            evaluateIndexService.insert(index);
            index.setCode("0102");
            index.setScore(tpl.getS0102());
            evaluateIndexService.insert(index);
            index.setCode("0103");
            index.setScore(tpl.getS0103());
            evaluateIndexService.insert(index);
            index.setCode("0104");
            index.setScore(tpl.getS0104());
            evaluateIndexService.insert(index);
            index.setCode("0105");
            index.setScore(tpl.getS0105());
            evaluateIndexService.insert(index);
            index.setCode("0201");
            index.setScore(tpl.getS2001());
            evaluateIndexService.insert(index);
            index.setCode("0202");
            index.setScore(tpl.getS0202());
            evaluateIndexService.insert(index);
            index.setCode("0203");
            index.setScore(tpl.getS0203());
            evaluateIndexService.insert(index);
            index.setCode("0204");
            index.setScore(tpl.getS0204());evaluateIndexService.insert(index);
            index.setCode("0205");
            index.setScore(tpl.getS0205());evaluateIndexService.insert(index);
            index.setCode("03");
            index.setScore(tpl.getS03());evaluateIndexService.insert(index);
            index.setCode("0401");
            index.setScore(tpl.getS0401());evaluateIndexService.insert(index);
            index.setCode("0402");
            index.setScore(tpl.getS0402());evaluateIndexService.insert(index);
            index.setCode("0403");
            index.setScore(tpl.getS0403());evaluateIndexService.insert(index);
            index.setCode("0404");
            index.setScore(tpl.getS0404());evaluateIndexService.insert(index);
            index.setCode("0405");
            index.setScore(tpl.getS0405());evaluateIndexService.insert(index);
            index.setCode("0406");
            index.setScore(tpl.getS0406());evaluateIndexService.insert(index);
            index.setCode("0407");
            index.setScore(tpl.getS0407());evaluateIndexService.insert(index);
            index.setCode("050101");
            index.setScore(tpl.getS050101());evaluateIndexService.insert(index);
            index.setCode("050102");
            index.setScore(tpl.getS050102());evaluateIndexService.insert(index);
            index.setCode("0502");
            index.setScore(tpl.getS0502());evaluateIndexService.insert(index);
            index.setCode("0503");
            index.setScore(tpl.getS0503());evaluateIndexService.insert(index);
            index.setCode("0504");
            index.setScore(tpl.getS0504());evaluateIndexService.insert(index);
            index.setCode("050501");
            index.setScore(tpl.getS050501());evaluateIndexService.insert(index);
            index.setCode("050502");
            index.setScore(tpl.getS050502());evaluateIndexService.insert(index);
            index.setCode("06");
            index.setScore(tpl.getS06());evaluateIndexService.insert(index);
            index.setCode("0701");
            index.setScore(tpl.getS0701());evaluateIndexService.insert(index);
            index.setCode("0702");
            index.setScore(tpl.getS0702());evaluateIndexService.insert(index);
            index.setCode("08");
            index.setScore(tpl.getS08());evaluateIndexService.insert(index);
            index.setCode("09");
            index.setScore(tpl.getS09());evaluateIndexService.insert(index);
            index.setCode("1001");
            index.setScore(tpl.getS1001());evaluateIndexService.insert(index);
            index.setCode("1002");index.setScore(tpl.getS1002());evaluateIndexService.insert(index);
            index.setCode("1003");index.setScore(tpl.getS1003());evaluateIndexService.insert(index);
            index.setCode("1004");index.setScore(tpl.getS1004());evaluateIndexService.insert(index);
            index.setCode("1101");index.setScore(tpl.getS1101());evaluateIndexService.insert(index);
            index.setCode("1102");index.setScore(tpl.getS1102());evaluateIndexService.insert(index);
            index.setCode("1103");index.setScore(tpl.getS1103());evaluateIndexService.insert(index);
            index.setCode("12");index.setScore(tpl.getS12());evaluateIndexService.insert(index);
            index.setCode("1301");index.setScore(tpl.getS1301());evaluateIndexService.insert(index);
            index.setCode("1302");index.setScore(tpl.getS1301());evaluateIndexService.insert(index);
            index.setCode("14");index.setScore(tpl.getS14());evaluateIndexService.insert(index);
            index.setCode("15");index.setScore(tpl.getS15());evaluateIndexService.insert(index);
            index.setCode("16");index.setScore(tpl.getS16());evaluateIndexService.insert(index);
            index.setCode("17");index.setScore(tpl.getS17());evaluateIndexService.insert(index);
            index.setCode("18");index.setScore(tpl.getS18());evaluateIndexService.insert(index);
            index.setCode("1901"); index.setScore(tpl.getS1901());evaluateIndexService.insert(index);
            index.setCode("1902");index.setScore(tpl.getS1902());evaluateIndexService.insert(index);
            index.setCode("2001");index.setScore(tpl.getS2001());evaluateIndexService.insert(index);
            index.setCode("2002");index.setScore(tpl.getS2002());evaluateIndexService.insert(index);
            index.setCode("2003");index.setScore(tpl.getS2003());evaluateIndexService.insert(index);
            index.setCode("21");index.setScore(tpl.getS21());evaluateIndexService.insert(index);
            index.setCode("2201");index.setScore(tpl.getS2201());evaluateIndexService.insert(index);
            index.setCode("2202");index.setScore(tpl.getS2202());evaluateIndexService.insert(index);
            index.setCode("23");index.setScore(tpl.getS23());evaluateIndexService.insert(index);
            index.setCode("24");index.setScore(tpl.getS24());evaluateIndexService.insert(index);
            index.setCode("25");index.setScore(tpl.getS25());evaluateIndexService.insert(index);
            index.setCode("26");index.setScore(tpl.getS26());evaluateIndexService.insert(index);
            index.setCode("2701");index.setScore(tpl.getS2701());evaluateIndexService.insert(index);
            index.setCode("2702");index.setScore(tpl.getS2702());evaluateIndexService.insert(index);
            index.setCode("2703");index.setScore(tpl.getS2703());evaluateIndexService.insert(index);
            index.setCode("2704");index.setScore(tpl.getS2704());evaluateIndexService.insert(index);
            index.setCode("2801");index.setScore(tpl.getS2801());evaluateIndexService.insert(index);
            index.setCode("2802");index.setScore(tpl.getS2802());evaluateIndexService.insert(index);
            index.setCode("29");index.setScore(tpl.getS29());evaluateIndexService.insert(index);
            index.setCode("30");index.setScore(tpl.getS30());evaluateIndexService.insert(index);
            index.setCode("31");index.setScore(tpl.getS31());evaluateIndexService.insert(index);
            index.setCode("32");index.setScore(tpl.getS32());evaluateIndexService.insert(index);
            index.setCode("33");index.setScore(tpl.getS33());evaluateIndexService.insert(index);
            index.setCode("34");index.setScore(tpl.getS34());evaluateIndexService.insert(index);
            index.setCode("3501");index.setScore(tpl.getS3501());evaluateIndexService.insert(index);
            index.setCode("3502");index.setScore(tpl.getS3502());evaluateIndexService.insert(index);
            index.setCode("3503");index.setScore(tpl.getS3503());evaluateIndexService.insert(index);
            index.setCode("36");index.setScore(tpl.getS36());evaluateIndexService.insert(index);
            index.setCode("37");index.setScore(tpl.getS37());evaluateIndexService.insert(index);
            index.setCode("38");index.setScore(tpl.getS38());evaluateIndexService.insert(index);
            index.setCode("39");index.setScore(tpl.getS39());evaluateIndexService.insert(index);
            index.setCode("40");index.setScore(tpl.getS40());evaluateIndexService.insert(index);
            index.setCode("4101");index.setScore(tpl.getS4101());evaluateIndexService.insert(index);
            index.setCode("4102");index.setScore(tpl.getS4102());evaluateIndexService.insert(index);
            index.setCode("4201");index.setScore(tpl.getS4201());evaluateIndexService.insert(index);
            index.setCode("4202");index.setScore(tpl.getS4202());evaluateIndexService.insert(index);
            index.setCode("43");index.setScore(tpl.getS43());evaluateIndexService.insert(index);
            index.setCode("4401");index.setScore(tpl.getS4401());evaluateIndexService.insert(index);
            index.setCode("4402");index.setScore(tpl.getS4402());evaluateIndexService.insert(index);
            index.setCode("45");index.setScore(tpl.getS45());evaluateIndexService.insert(index);
            index.setCode("46");index.setScore(tpl.getS46());evaluateIndexService.insert(index);


        }
    }
}
