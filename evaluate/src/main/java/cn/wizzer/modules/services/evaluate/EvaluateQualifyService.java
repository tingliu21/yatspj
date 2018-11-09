package cn.wizzer.modules.services.evaluate;

import cn.wizzer.common.base.Service;
import cn.wizzer.modules.models.evaluate.Evaluate_qualify;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.text.NumberFormat;
import java.text.ParseException;

@IocBean(args = {"refer:dao"})
public class EvaluateQualifyService extends Service<Evaluate_qualify> {
	private static final Log log = Logs.get();

    public EvaluateQualifyService(Dao dao) {
    	super(dao);
    }

    public Boolean isQualified(String svalue, String formula){
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");
        String expression = "";
        String[] target = formula.split(";");
        String[] value = svalue.split(";");
        try{
            for(int i =0;i<target.length;i++){
                String[] target_l = target[i].split(",");
                expression += "(";
                for(int j=0;j<target_l.length;j++){
                    if(value[i].indexOf("%")!=-1){
                        NumberFormat nf= NumberFormat.getPercentInstance();
                        Number m=nf.parse(value[i]);
                        expression += m + target_l[j] +"&&";
                    }else
                        expression += value[i] + target_l[j] +"&&";
                }
                expression = expression.substring(0,expression.length()-2);
                expression +=")&&";
            }
            expression = expression.substring(0,expression.length()-2);

            return  (Boolean) engine.eval(expression);
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (ScriptException e) {
            e.printStackTrace();
        }
        return null;

    }
}

