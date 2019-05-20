package cn.wizzer.modules.controllers.platform.basicdata;

import cn.wizzer.common.annotation.SLog;
import cn.wizzer.common.base.Result;
import cn.wizzer.common.filter.PrivateFilter;
import cn.wizzer.common.page.DataTableColumn;
import cn.wizzer.common.page.DataTableOrder;
import cn.wizzer.modules.models.basicdata.Bascidata_friendlyschool;
import cn.wizzer.modules.services.basicdata.BascidataFriendlyschoolService;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@IocBean
@At("/platform/basicdata/friendlyschool")
@Filters({@By(type = PrivateFilter.class)})
public class BascidataFriendlyschoolController {
	private static final Log log = Logs.get();
	@Inject
	private BascidataFriendlyschoolService bascidataFriendlyschoolService;

	@At("")
	@Ok("beetl:/platform/basicdata/friendlyschool/index.html")
	@RequiresAuthentication
	public void index() {

	}

	@At
	@Ok("json:full")
	@RequiresAuthentication
	public Object data(@Param("length") int length, @Param("start") int start, @Param("draw") int draw, @Param("::order") List<DataTableOrder> order, @Param("::columns") List<DataTableColumn> columns) {
		Cnd cnd = Cnd.NEW();
    	return bascidataFriendlyschoolService.data(length, start, draw, order, columns, cnd, null);
    }

    @At
    @Ok("beetl:/platform/basicdata/friendlyschool/add.html")
    @RequiresAuthentication
    public void add() {

    }

    @At
    @Ok("json")
    @SLog(tag = "新建Bascidata_friendlyschool", msg = "")
    public Object addDo(@Param("..") Bascidata_friendlyschool bascidataFriendlyschool, HttpServletRequest req) {
		try {
			bascidataFriendlyschoolService.insert(bascidataFriendlyschool);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }

    @At("/edit/?")
    @Ok("beetl:/platform/basicdata/friendlyschool/edit.html")
    @RequiresAuthentication
    public Object edit(String id) {
		return bascidataFriendlyschoolService.fetch(id);
    }

    @At
    @Ok("json")
    @SLog(tag = "修改Bascidata_friendlyschool", msg = "ID:${args[0].id}")
    public Object editDo(@Param("..") Bascidata_friendlyschool bascidataFriendlyschool, HttpServletRequest req) {
		try {

			bascidataFriendlyschool.setOpAt((int) (System.currentTimeMillis() / 1000));
			bascidataFriendlyschoolService.updateIgnoreNull(bascidataFriendlyschool);
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }


    @At({"/delete","/delete/?"})
    @Ok("json")
    @SLog(tag = "删除Bascidata_friendlyschool", msg = "ID:${args[2].getAttribute('id')}")
    public Object delete(String id, @Param("ids") String[] ids ,HttpServletRequest req) {
		try {
			if(ids!=null&&ids.length>0){
				bascidataFriendlyschoolService.delete(ids);
    			req.setAttribute("id", org.apache.shiro.util.StringUtils.toString(ids));
			}else{
				bascidataFriendlyschoolService.delete(id);
    			req.setAttribute("id", id);
			}
			return Result.success("system.success");
		} catch (Exception e) {
			return Result.error("system.error");
		}
    }


    @At("/detail/?")
    @Ok("beetl:/platform/basicdata/friendlyschool/detail.html")
    @RequiresAuthentication
	public Object detail(String id) {
		if (!Strings.isBlank(id)) {
			return bascidataFriendlyschoolService.fetch(id);

		}
		return null;
    }

}
