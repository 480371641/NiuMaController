package mainFesht3.niuMaManager.Utils;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class RandomUtils {

    /**
     * 生成 [0, max) 范围内的随机整数
     */
    public static int randomInt(int max) {
        return new Random().nextInt(max);
    }

    /**
     * 生成 [min, max] 范围内的随机整数
     */
    public static int randomInt(int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException("最小值不能大于最大值");
        }
        return new Random().nextInt(max - min + 1) + min;
    }

    /**
     * 生成线程安全的 [min, max] 范围内的随机整数
     */
    public static int randomIntThreadSafe(int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException("最小值不能大于最大值");
        }
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    /**
     * 生成 [0.0, 1.0) 范围内的随机 double 数
     */
    public static double randomDouble() {
        return new Random().nextDouble();
    }

    /**
     * 生成 [min, max) 范围内的随机 double 数
     */
    public static double randomDouble(double min, double max) {
        if (min >= max) {
            throw new IllegalArgumentException("最小值必须小于最大值");
        }
        return new Random().nextDouble() * (max - min) + min;
    }

    /**
     * 生成线程安全的 [min, max) 范围内的随机 double 数
     */
    public static double randomDoubleThreadSafe(double min, double max) {
        if (min >= max) {
            throw new IllegalArgumentException("最小值必须小于最大值");
        }
        return ThreadLocalRandom.current().nextDouble(min, max);
    }

//    // 示例用法
//    public static void main(String[] args) {
//        System.out.println("随机整数 (0-9): " + randomInt(10));
//        System.out.println("随机整数 (5-15): " + randomInt(5, 15));
//        System.out.println("线程安全随机整数 (5-15): " + randomIntThreadSafe(5, 15));
//        System.out.println("随机 double (0.0-1.0): " + randomDouble());
//        System.out.println("随机 double (2.5-5.5): " + randomDouble(2.5, 5.5));
//        System.out.println("线程安全随机 double (2.5-5.5): " + randomDoubleThreadSafe(2.5, 5.5));
//    }
}