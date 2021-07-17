package cn.wondershare.UDF;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author kyrie
 * @create 2021-07-14 9:48
 */
public class getWorkDay {

    /**
     * 获取两个日期之间的所有日期
     *
     * @param startTime 开始日期
     * @param endTime   结束日期
     * @return
     */
    public List<String> getDays(String startTime, String endTime) {
        // 返回的日期集合
        List<String> days = new ArrayList<>();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date start = dateFormat.parse(startTime);
            Date end = dateFormat.parse(endTime);
            Calendar tempStart = Calendar.getInstance();
            tempStart.setTime(start);
            Calendar tempEnd = Calendar.getInstance();
            tempEnd.setTime(end);
            tempEnd.add(Calendar.DATE, +1);// 日期加1(包含结束)
            while (tempStart.before(tempEnd)) {
                days.add(dateFormat.format(tempStart.getTime()));
                tempStart.add(Calendar.DAY_OF_YEAR, 1);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return days;
    }

    public static void getDaysByYear(int year) {
        Calendar c = Calendar.getInstance();
        //List dates = new ArrayList();
        for (int i = 0; i < 12; i++) {
            c.set(year, i, 1);
            int lastDay = c.getActualMaximum(Calendar.DATE);
            for (int j = 1; j <= lastDay; j++) {
                String month = "";
                String day = "";
                if (i < 9) month = "-0" + (i + 1);
                else month = "-" + (i + 1);
                if (j < 10) day = "-0" + j;
                else day = "-" + j;
                String date = year + month + day;
                CourseList(date);
                //System.out.println(date);
                //dates.add(date);
            }
        }
        //return dates;
    }

    /**
     * 将字符串追加到文件已有内容后面
     *
     * @param fileFullPath 文件完整地址：D:/test.txt
     * @param content      需要写入的
     */
    public static void writeFile(String fileFullPath, String content) {
        FileOutputStream fos = null;
        try {
            //true不覆盖已有内容
            fos = new FileOutputStream(fileFullPath, true);
            //写入
            fos.write(content.getBytes());
            // 写入一个换行
            fos.write("\r\n".getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 方法描述：方法描述：获取节假日 访问接口，根据返回值判断当前日期是否为工作日，
     * 返回结果：检查具体日期是否为节假日，工作日对应结果为 0, 周末对应结果为 1, 节假日对应的结果为 2；
     * https://timor.tech/api/holiday/info/
     */
    public static String CourseList(String time) {
        String url = "https://timor.tech/api/holiday/info/";
        String httpUrl = new StringBuffer().append(url).append(time).toString();
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(httpUrl);
        String json = null;
        try {
            // 通过HttpClient Get请求返回Json数据
            HttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                json = EntityUtils.toString(entity, "UTF-8").trim();
                JSONObject jsonModelObject = JSON.parseObject(json);
                String model = jsonModelObject.getString("type");
                JSONObject jsonCurPageDataObj = JSON.parseObject(model);
                //获取类型 工作日对应结果为 0, 周末对应结果为 1, 节假日对应的结果为 2
                int result = jsonCurPageDataObj.getIntValue("type");
                //获取名称
                String isWork = jsonCurPageDataObj.getString("name");
                if (result == 1) {
                    //周末对应结果为 1
                    System.out.println(time + ",3," + isWork);
                    writeFile("D:/day_work123.txt", time + ",3," + isWork);
                } else if (result == 2) {
                    //节假日对应的结果为 2
                    System.out.println(time + ",2," + isWork);
                    writeFile("D:/day_work123.txt", time + ",3," + isWork);
                } else {
                    //工作日对应结果为 0
                    System.out.println(time + ",1,工作日");
                    writeFile("D:/day_work123.txt", time + ",1,工作日");
                }
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            httpGet.abort();
        }
        return "success";
    }


    public static void main(String[] args) {
        String time = "2021-06-15";
        //String dateFlag = CourseList(time);
        getDaysByYear(2021);
    }
}
