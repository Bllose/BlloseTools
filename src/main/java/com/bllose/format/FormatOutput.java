package com.bllose.format;

import java.util.List;
import java.util.Map;

/**
 * <pre>
 * 按照某种标准输出
 * </pre>
 *
 * @Author Bllose
 * @Date 2019/5/25 23:34
 */
public class FormatOutput {

    /**
     * 将列表中的信息按列打印出来
     * @Author Bllose
     * @Date 2019/5/25 23:37
     * @param informationList 需要被打印的信息集合
     * @param max_size  单列信息中最长信息长度
     * <pre>
     *    最外层List是总的信息列数
     *    内层Map的key值为该列的tital
     *    内层List中的值为该列的具体信息
     * </pre>
     */
    public static void printTidy(List<Map<String, List<String>>> informationList, int max_size){
        // 需要展示多少列
        int count = informationList.size();
        int length = max_size + 5;

        String eachLine = "";
        List[] informationCollection = new List[count];

        // 首先展示台头
        for(int i = 0 ; i < count; i ++){
            Map<String, List<String>> inforMap = informationList.get(i);
            String key = inforMap.keySet().iterator().next();
            informationCollection[i] = inforMap.get(key);
            eachLine += String.format("%-"+length+"s", key);
        }
        System.out.println(eachLine);

        printEachline(informationCollection, length);
    }

    private static void printEachline(List[] informationCollection, int length) {
        int deep = 0;
        for(Object o : informationCollection){
            int deepTemp = ((List) o).size();
            if(deepTemp > deep){
                deep = deepTemp;
            }
        }


        for(int i = 0 ; i < length; i ++){
            String eachLine = "";
            for(Object o : informationCollection){
                List<String> information = (List<String>) o;
                if(information.size() > i){
                    eachLine += String.format("%-"+length+"s", information.get(i));
                }else{
                    eachLine += String.format("%-"+length+"s", "");
                }
            }
            System.out.println(eachLine);
        }
    }
}
