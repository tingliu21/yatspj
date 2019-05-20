package cn.wizzer.common.util;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Map;

public class XwpfUtil {
    /**
     * 导出word文件
     * @param params
     * @param is
     * @param request
     * @param response
     * @param xwpfUtil
     */
    public void exportWord(Map<String, Object> wordDataMap, InputStream is,
                           HttpServletResponse response, String filename) {
        try {

            OutputStream os = response.getOutputStream();
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-disposition","attachment;filename="+filename);//文件名中文不显示

//            File file = new File("/Users/maofeng/Documents/GitHub/yatspj/evaluate/target/nutzwk/WEB-INF/classes/template/SelfEvaluate.docx");//改成你本地文件所在目录

            // 读取word模板
//            FileInputStream fileInputStream = new FileInputStream(file);
            WordTemplate template = new WordTemplate(is);

            // 替换数据
            template.replaceDocument(wordDataMap);

            template.getDocument().write(os);

            //关闭流
            close(os);
            close(is);
            os.flush();
            os.close();
        } catch (IOException e) {
//            logger.error("文件导出错误");
        }
    }

    /**
     * 关闭输入流
     * @param is
     */
    public void close(InputStream is) {
        if (is != null) {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 关闭输出流
     * @param is
     */
    public void close(OutputStream os) {
        if (os != null) {
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
