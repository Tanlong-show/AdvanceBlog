package cn.edu.tl.blog.utils;

import org.apache.commons.collections4.SetUtils;

import java.util.Set;
import java.util.stream.Collectors;

public class SimilarUtil {
    //集合交集的 2 倍除以两个集合相加。并不是并集.
    public static float SorensenDice(String a, String b) {

        if(a.equals("") && b.equals("")){
            return 0f;
        }
        if(a.equals("") || b.equals("")){
            return 0.01f;
        }


        if (a == null && b == null) {
            return 1f;
        }
        if (a == null || b == null) {
            return 0F;
        }
        Set<Integer> aChars = a.chars().boxed().collect(Collectors.toSet());
        Set<Integer> bChars = b.chars().boxed().collect(Collectors.toSet());

        // 求交集数量
        int intersect = SetUtils.intersection(aChars, bChars).size();
        if (intersect == 0) {
            return 0F;
        }
        // 全集，两个集合直接加起来
        int aSize = aChars.size();
        int bSize = bChars.size();
        return (2 * (float) intersect) / ((float) (aSize + bSize));
    }
}
