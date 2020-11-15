package net.hznu.modules.controllers.platform.evaluate;

import net.hznu.common.annotation.SLog;
import net.hznu.common.base.Globals;
import net.hznu.common.base.Result;
import net.hznu.common.filter.PrivateFilter;
import net.hznu.common.page.DataTableColumn;
import net.hznu.common.page.DataTableOrder;
import net.hznu.modules.models.evaluate.Evaluate_index;
import net.hznu.modules.models.evaluate.Evaluate_records;
import net.hznu.modules.models.monitor.Monitor_index;
import net.hznu.modules.models.sys.Sys_unit;
import net.hznu.modules.models.sys.Sys_user;
import net.hznu.modules.services.evaluate.EvaluateIndexService;
import net.hznu.modules.services.evaluate.EvaluateRecordsService;
import net.hznu.modules.services.evaluate.EvaluateSpecialService;
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
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.*;
import org.nutz.mvc.impl.AdaptorErrorContext;
import org.nutz.mvc.upload.TempFile;
import org.nutz.mvc.upload.UploadAdaptor;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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
    private SysUnitService sysUnitService;
    @Inject
    private EvaluateSpecialService evaluateSpecialService;


    @At("")
    @Ok("beetl:/platform/evaluate/records/index.html")
    @RequiresAuthentication
    public void index(HttpServletRequest req) {
        req.setAttribute("cityList",sysUnitService.query(Cnd.where("level","=",2).and("unitcode","like","33%").asc("unitcode")));
    }

    @At("/special")
    @Ok("beetl:/platform/evaluate/records/specialindex.html")
    @RequiresAuthentication
    public void specindex( HttpServletRequest req) {

    }
    @At
    @Ok("json")
    @RequiresAuthentication
    public Object getEvaluateId(@Param("unitcode")String unitcode,@Param("year")int year) {

        Evaluate_records record=evaluateRecordsService.fetch(Cnd.where("unitcode","=",unitcode).and("year","=",year));
        return record.getId();
    }
    @At
    @Ok("json:full")
    @RequiresAuthentication
    public Object data(@Param("year") int year,@Param("unitcode") String unitcode,@Param("xzqhmc") String xzqhmc,@Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {
        Cnd cnd = Cnd.NEW();
        //获取当前用户id
        Subject subject = SecurityUtils.getSubject();
        Sys_user user = (Sys_user) subject.getPrincipal();
        Sys_unit sysUnit = user.getUnit();
        String xzqhdm=sysUnit.getUnitcode();
        //这里的xzqhdm是登陆用户的，用于用户只能看自己的数据
        if(!Strings.isEmpty(xzqhdm)) {
            String statXZQ=xzqhdm;
            if(xzqhdm.endsWith("00")) {
                statXZQ = xzqhdm.substring(0, xzqhdm.lastIndexOf("00"));
                if (statXZQ.endsWith("00")) {
                    statXZQ = statXZQ.substring(0, statXZQ.lastIndexOf("00"));
                }
            }
            cnd.and("unitcode", "like", statXZQ+"%");
        }
        if(year!=0) {
            cnd.and("year", "=", year);
        }
        if(!Strings.isEmpty(unitcode)) {
            cnd.and("unitcode", "like", unitcode.substring(0, 4) + "%");
        }
        if(!Strings.isEmpty(xzqhmc)) {
            cnd.and("xzqhmc", "like", "%"+xzqhmc + "%");
        }
        //cnd.desc("year").asc("unitcode");
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
                    List<Monitor_index> monitorIndexs = monitorIndexService.query(Cnd.where("year", "=", year).and("haschildren","=",false));

                    for (Monitor_index index : monitorIndexs) {
                        Evaluate_index evaluate = new Evaluate_index();
                        evaluate.setEvaluateId(records.getId());
                        evaluate.setIndexId(index.getId());
                        evaluate.setCode(index.getCode());
                        evaluate.setYear(year);
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
    public void upload() {
    }
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
                }else{
                //通过value字段和sql语句得到svalue字段
                    evaluateRecordsService.UpdateIndexSvalue();
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

}
