package test;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 利润计算器
 *
 * @author 0neBean
 * @version 1.0
 * @since 2021/8/3 0:19
 */
public class ProfitsCalculator {

    private final static boolean SHOW_DAY_PROFITS = true;
    private final static boolean SHOW_MONTH_PROFITS = false;
    private final static int DAY_LOOP = 30 * 6;
    private final static int DAY_TIMES = 3;
    private static BigDecimal START_USDT = new BigDecimal("950");
    private final static BigDecimal RMB_HL = new BigDecimal("6.4");
    private final static BigDecimal profits_rate = new BigDecimal("1");


    public static void main(String[] args) {
        System.out.println("start money = " + START_USDT.multiply(RMB_HL).setScale(2, RoundingMode.HALF_UP));

        int k = 0;
        BigDecimal monthProfits = new BigDecimal("0");
        for (int j = 0; j < DAY_LOOP; j++) {
            k++;
            BigDecimal dayProfits = new BigDecimal("0");
            for (int i = 0; i < DAY_TIMES; i++) {
                BigDecimal profits = START_USDT.multiply(new BigDecimal("0.05")).multiply(new BigDecimal("0.2")).multiply(profits_rate);
                START_USDT = START_USDT.add(profits);
                dayProfits = dayProfits.add(profits);
            }
            if (k == 30) {
                if (SHOW_MONTH_PROFITS) {
                    System.out.println("month Profits = " + monthProfits.multiply(RMB_HL).setScale(2, RoundingMode.HALF_UP));
                }
                k = 0;
            } else {
                monthProfits = monthProfits.add(dayProfits);
            }
            if (SHOW_DAY_PROFITS) {
                BigDecimal oneDay = dayProfits.multiply(RMB_HL);
                System.out.println("day = " + j + " , oneDay = " + oneDay.setScale(2, RoundingMode.HALF_UP));
            }
        }

        System.out.println("end money = " + START_USDT.multiply(RMB_HL).setScale(2, RoundingMode.HALF_UP));
    }
}
