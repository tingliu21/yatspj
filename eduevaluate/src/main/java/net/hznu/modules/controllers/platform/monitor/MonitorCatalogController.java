package net.hznu.modules.controllers.platform.monitor;

import net.hznu.common.annotation.SLog;
import net.hznu.common.base.Result;
import net.hznu.common.filter.PrivateFilter;
import net.hznu.common.page.DataTableColumn;
import net.hznu.common.page.DataTableOrder;
import net.hznu.modules.models.monitor.Monitor_catalog;
import net.hznu.modules.services.monitor.MonitorCatalogService;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@IocBean
@At("/platform/monitor/catalog/")
@Filters({@By(type = PrivateFilter.class)})
public class MonitorCatalogController {
	private static final Log log = Logs.get();
	@Inject
	private MonitorCatalogService monitorCatalogService;

	@At("")
	@Ok("beetl:/platform/monitor/catalog/index.html")
	@RequiresAuthentication
	public void index(HttpServletRequest req) {

		///where (A or B) and C
		req.setAttribute("list", monitorCatalogService.query(Cnd.where(Cnd.exps("parentId", "=", "").or("parentId", "is", null))
						.asc("catacode")));

	}

	@At
	@Ok("json:full")
	@RequiresAuthentication
	public Object data(@Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {
		Cnd cnd = Cnd.NEW();
    	return monitorCatalogService.data(length, start, draw, order, columns, cnd, null);
    }

    @At
    @Ok("beetl:/platform/monitor/catalog/add.html")
    @RequiresAuthentication
    public Object add(@Param("pid") String pid,@Param("year") int year, HttpServletRequest req) {
		if(year!=0){
			req.setAttribute("year",year);
		}
		return Strings.isBlank(pid) ? null : monitorCatalogService.fetch(pid);
    }

    @At
    @Ok("json")
	@RequiresPermissions("monitor.catalog.add")
    @SLog(tag = "新建指标体系", msg = "指标体系名称：${args[0].name}")
    public Object addDo(@Param("..") Monitor_catalog monitorCatalog, @Param("parentId") String parentId, HttpServletRequest req) {
		try {
			monitorCatalogService.save(monitorCatalog,parentId);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

    @At("/edit/?")
    @Ok("beetl:/platform/monitor/catalog/edit.html")
    @RequiresAuthentication
    public Object edit(String id, HttpServletRequest req) {
		Monitor_catalog catalog = monitorCatalogService.fetch(id);
		if(catalog!=null) {
			req.setAttribute("parentMenu", monitorCatalogService.fetch(catalog.getParentId()));
		}
		return catalog;
    }

    @At
    @Ok("json")
	@RequiresPermissions("monitor.catalog.edit")
    @SLog(tag = "修改指标体系", msg = "指标体系名称:${args[0].name}")
    public Object editDo(@Param("..") Monitor_catalog monitorCatalog, HttpServletRequest req) {
		try {
			monitorCatalog.setOpBy(Strings.sNull(req.getAttribute("uid")));
			monitorCatalog.setOpAt((int) (System.currentTimeMillis() / 1000));
			monitorCatalogService.updateIgnoreNull(monitorCatalog);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }


    @At({"/delete","/delete/?"})
    @Ok("json")
	@RequiresPermissions("monitor.catalog.delete")
    @SLog(tag = "删除指标体系", msg = "指标体系名称:${args[1].getAttribute('name')}")
    public Object delete(String id,HttpServletRequest req) {
		try {
			Monitor_catalog catalog = monitorCatalogService.fetch(id);
			req.setAttribute("name",catalog.getName());
			monitorCatalogService.deleteAndChild(catalog);

			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }
	// Add by Liut 2018-10-29
	@At({"/tree","/tree/?"})
	@Ok("json")
	@RequiresAuthentication
	public Object tree(@Param("year") int year,@Param("pid") String pid) {
		Cnd cnd = Cnd.where("parentId", "=", Strings.sBlank(pid));
		if(year!=0){
			cnd = cnd.and("year","=",year);
		}
		List<Monitor_catalog> list = monitorCatalogService.query(cnd.asc("catacode"));
		List<Map<String, Object>> tree = new ArrayList<>();
		for (Monitor_catalog catalog : list) {
			Map<String, Object> obj = new HashMap<>();
			obj.put("id", catalog.getId());
			obj.put("text", catalog.getName());
			obj.put("children", catalog.isHasChildren());
			tree.add(obj);
		}
		return tree;
	}
	@At
	@Ok("json")
	@RequiresAuthentication
	//仅列出2级指标 add by Liut 2019-6-10
	public Object tree2(@Param("year") int year) {
		Cnd cnd = Cnd.where("year", "=", year);

		List<Monitor_catalog> list = monitorCatalogService.query(cnd.asc("catacode"));
		List<Map<String, Object>> tree = new ArrayList<>();
		for (Monitor_catalog catalog : list) {
			Map<String, Object> obj = new HashMap<>();
			obj.put("id", catalog.getCatacode());
			String strName = "";
			int i = catalog.getLevel();
			while(i>1){
				strName+="&nbsp;&nbsp;";
				i--;
			}
			strName += catalog.getName();
			obj.put("text", strName);

			tree.add(obj);
		}
		return tree;
	}
	@At({"/child","/child/?"})
	@Ok("beetl:/platform/monitor/catalog/child.html")
	@RequiresAuthentication
	public Object child(String id,@Param("year") int year) {
		if(id == null){
			id="";
		}
		Cnd cnd = Cnd.where("parentId", "=", id);
		if(year!=0){
			cnd = cnd.and("year","=",year);
		}
		return monitorCatalogService.query(cnd.asc("catacode"));
	}


	@At("/detail/?")
    @Ok("beetl:/platform/monitor/catalog/detail.html")
    @RequiresAuthentication
	public Object detail(String id) {
		if (!Strings.isBlank(id)) {
			return monitorCatalogService.fetch(id);

		}
		return null;
    }

}
