package com.tkfc.boot.starter.opencv.toolkit;

import com.tkfc.boot.starter.opencv.model.ChartPoint;
import com.tkfc.boot.starter.opencv.model.ChartPointColorCount;
import com.tkfc.boot.starter.opencv.model.ChartPointMatcher;
import com.tkfc.core.constants.StringPool;
import com.tkfc.core.function.SerializableCiConsumer;
import com.tkfc.core.throwable.base.Assert;
import com.tkfc.core.toolkit.NumberUtil;
import com.tkfc.core.toolkit.StringUtil;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Mat 工具类
 *
 * @author 0neBean
 * @version 1.0
 * @since 2022-01-30 00:38:54
 */
public class MatUtil {

    /**
     * base64 展示前缀
     */
    public final static String BASE64_PREFIX = "data:image/png;base64,";

    /**
     * 创建纯色的mat对象
     *
     * @param width       宽
     * @param height      搞
     * @param targetPoint 点位对象
     * @return mat
     */
    public static Mat createPureColorMat(int width, int height, ChartPoint targetPoint) {
        Mat mat = new Mat(height, width, CvType.CV_8UC3);
        eachMat(mat, (point, row, col) -> {
            double[] ragArray = coverChartPointToRgbArray(targetPoint);
            mat.put(row, col, ragArray);
        });
        return mat;
    }

    /**
     * 获取两线的交叉点
     *
     * @param lineA 线a
     * @param lineB 线b
     * @return 交叉点
     * @author liwenbin
     * @since 2022/11/27 15:59
     */
    public static List<ChartPoint> getCrossPoints(List<ChartPoint> lineA, List<ChartPoint> lineB) {
        List<ChartPoint> result = new ArrayList<>();
        Map<Integer, ChartPoint> xMapA = lineA.stream().collect(Collectors.toMap(ChartPoint::getX, Function.identity()));
        List<ChartPoint> tempALine = new ArrayList<>(lineA);
        List<ChartPoint> tempBLine = new ArrayList<>(lineB);
        tempALine.sort(Comparator.comparing(ChartPoint::getX).reversed());
        tempBLine.sort(Comparator.comparing(ChartPoint::getX).reversed());
        for (ChartPoint itemB : tempBLine) {
            Integer xB = itemB.getX();
            Integer yB = itemB.getY();
            if (!xMapA.containsKey(xB)) {
                continue;
            }

            if (result.size() > 1) {
                ChartPoint last = result.get(result.size() - 1);
                int offsetX = Math.abs(xB - last.getX());
                if (offsetX < 3) {
                    continue;
                }
            }

            ChartPoint preA = getPreviousNode(tempALine, xB, 1);
            ChartPoint preB = getPreviousNode(tempBLine, xB, 1);
            if (Objects.isNull(preA) || Objects.isNull(preB)) {
                continue;
            }
            Integer yAPre = preA.getY();
            Integer yBPre = preB.getY();
            Integer yA = xMapA.get(xB).getY();
            BigDecimal preYSubtractVal = new BigDecimal(yAPre).subtract(new BigDecimal(yBPre));
            BigDecimal currentYSubtractVal = new BigDecimal(yA).subtract(new BigDecimal(yB));
            boolean previousFlip = NumberUtil.isMinus(preYSubtractVal);
            boolean currentFlip = NumberUtil.isMinus(currentYSubtractVal);
            if (!Objects.equals(previousFlip, currentFlip) || (NumberUtil.aEQb(currentYSubtractVal,BigDecimal.ZERO))) {
                result.add(ChartPoint.builder().x(xB).y(yB).build());
            }
        }
        return result;
    }

    /**
     * 获取前一个节点
     *
     * @param line 线
     * @param x    x
     * @param skip 跳过的数量
     * @return 前一个节点
     */
    public static ChartPoint getPreviousNode(List<ChartPoint> line, Integer x, Integer skip) {
        for (int i = 0; i < line.size(); i++) {
            if (Objects.equals(line.get(i).getX(), x)) {
                if (i + skip < line.size() - 1) {
                    return line.get(i + skip);
                }
            }
        }
        return null;
    }

    /**
     * 过滤单调递增
     *
     * @param line        线
     * @param checkWindow 检查窗口
     * @param sameCount   y坐标重复次数
     * @return 单调递增的 的 x
     */
    public static Set<Integer> filterMonotoneIncreasing(List<ChartPoint> line, Integer checkWindow, Integer sameCount) {
        //获取DMI陡峭节点
        Map<Integer, Integer> xMapLine = new HashMap<>();
        for (ChartPoint point : line) {
            xMapLine.put(point.getX(), point.getY());
        }
        List<Integer> xMapKeys = new ArrayList<>(xMapLine.keySet());
        Set<Integer> result = new HashSet<>();
        for (int i = 0; i < xMapKeys.size(); i++) {
            if (i < checkWindow) {
                continue;
            }
            int sameDxCount = 0;
            boolean monotoneIncreasing = true;
            int tempY = -1;
            for (int j = i; j > i - checkWindow; j--) {
                Integer y = xMapLine.get(xMapKeys.get(j));
                if (y == tempY) {
                    ++sameDxCount;
                } else {
                    sameDxCount = 0;
                }
                if (sameDxCount > sameCount) {
                    monotoneIncreasing = false;
                    break;
                }
                if (tempY <= y) {
                    tempY = y;
                } else {
                    monotoneIncreasing = false;
                    tempY = -1;
                }
            }
            if (monotoneIncreasing) {
                result.add(xMapKeys.get(i));
            }
        }
        return result;
    }

    /**
     * 根据点获取线
     *
     * @param image       图片
     * @param targetPoint 目标点位
     * @param smooth      是否平滑处理
     * @return 读取的结果
     */
    public static List<ChartPoint> getLineByPoint(Mat image, ChartPoint targetPoint, Boolean smooth) {
        return getLineByPoint(image, Collections.singletonList(targetPoint), smooth);
    }

    /**
     * 根据点获取线
     *
     * @param image        图片
     * @param targetPoints 目标点位
     * @param smooth       是否平滑处理
     * @return 读取的结果
     */
    public static List<ChartPoint> getLineByPoint(Mat image, List<ChartPoint> targetPoints, Boolean smooth) {
        List<ChartPoint> result = new ArrayList<>();
        eachMat(image, (point, row, col) -> {
            double tempB = point[0];
            double tempG = point[1];
            double tempR = point[2];
            for (ChartPoint targetPoint : targetPoints) {
                double r = targetPoint.getR();
                double g = targetPoint.getG();
                double b = targetPoint.getB();
                //完全命中
                boolean fullHit = Objects.equals(tempB, b) && Objects.equals(tempG, g) && Objects.equals(tempR, r);
                if (fullHit) {
                    ChartPoint chartPoint = ChartPoint.builder().x(col).y(row).r(tempR).g(tempG).b(tempB).build();
                    result.add(chartPoint);
                }
            }
        });
        return (smooth) ? smooth(result) : result;
    }

    /**
     * 给目标线条插帧
     *
     * @param targetLine 目标线条
     * @return smooth line
     * @author 0neBean
     * @since 2022/11/27 15:57
     */
    private static List<ChartPoint> smooth(List<ChartPoint> targetLine) {
        //x轴排序
        targetLine.sort(Comparator.comparing(ChartPoint::getX));
        List<ChartPoint> smoothList = new ArrayList<>();
        //遍历x轴
        for (ChartPoint current : targetLine) {
            //第一个节点必须插入
            if (smoothList.size() < 1) {
                smoothList.add(current);
                continue;
            }
            //获取上一个节点
            ChartPoint last = smoothList.get(smoothList.size() - 1);
            //免插帧判断
            if (Objects.equals(current.getX(), last.getX())) {
                continue;
            }
            List<ChartPoint> addFrameList = new ArrayList<>();
            int offsetX = current.getX() - last.getX();
            int offsetY = current.getY() - last.getY();
            int addY = 0;
            if (offsetX > 1) {
                for (int i = 0; i < offsetX - 1; i++) {
                    int tempY;
                    if (offsetY < 0) {
                        --addY;
                        tempY = (Math.abs(addY) <= offsetY) ? last.getY() - 1 : last.getY();
                    } else if (offsetY > 0) {
                        ++addY;
                        tempY = (Math.abs(addY) <= offsetY) ? last.getY() + 1 : last.getY();
                    } else {
                        tempY = last.getY();
                    }
                    int tempX = last.getX() + 1;
                    ChartPoint currentAdd = ChartPoint.builder().x(tempX).y(tempY).r(current.getR()).g(current.getG()).b(current.getB()).build();
                    addFrameList.add(currentAdd);
                    last = currentAdd;
                }
            }
            addFrameList.add(current);
            smoothList.addAll(addFrameList);
        }
        return smoothList;
    }

    /**
     * 获取分布最多的前三个颜色
     *
     * @param image 图片
     * @return int
     * @author 0neBean
     * @since 2022-01-29 22:19:10
     */
    public static List<ChartPointColorCount> getTopSpreadColorList(Mat image) {
        Map<String, Integer> colorCount = new HashMap<>(16);
        List<ChartPointColorCount> result = new ArrayList<>();
        eachMat(image, (point, row, col) -> {
            double tempB = point[0];
            double tempG = point[1];
            double tempR = point[2];
            String countKey = StringUtil.concat(String.valueOf(tempR), StringPool.DASH, String.valueOf(tempG), StringPool.DASH, String.valueOf(tempB));
            int count = colorCount.containsKey(countKey) ? colorCount.get(countKey) + 1 : 1;
            colorCount.put(countKey, count);
        });
        colorCount.forEach((k, v) -> {
            String[] keyArray = k.split(StringPool.DASH);
            ChartPoint point = ChartPoint.builder().r(Double.valueOf(keyArray[0])).g(Double.valueOf(keyArray[1])).b(Double.valueOf(keyArray[2])).build();
            result.add(ChartPointColorCount.builder().chartPoint(point).count(v).build());
        });
        result.sort(Comparator.comparing(ChartPointColorCount::getCount));
        Collections.reverse(result);
        return result;
    }

    /**
     * 遍历mat
     *
     * @param image 图片
     * @param each  遍历逻辑
     * @author 0neBean
     * @since 2022-01-29 22:17:49
     */
    public static void eachMat(Mat image, SerializableCiConsumer<double[], Integer, Integer> each) {
        if (Objects.isNull(image) || image.empty()) {
            return;
        }
        int cols = image.cols();
        int rows = image.rows();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                each.accept(image.get(i, j), i, j);
            }
        }
    }

    /**
     * 匹配图片中的点
     *
     * @param image   图片
     * @param matcher 匹配对象
     * @author 0neBean
     * @since 2022/11/26 15:30
     */
    public static void matchXAndY(Mat image, ChartPointMatcher matcher) {
        eachMat(image, (point, row, col) -> {
            double tempB = point[0];
            double tempG = point[1];
            double tempR = point[2];
            List<ChartPoint> chartPoints = matcher.getChartPoints();
            for (ChartPoint chartPoint : chartPoints) {
                if (Objects.equals(row, chartPoint.getY()) && Objects.equals(col, chartPoint.getX())) {
                    ChartPoint matchPoint = ChartPoint.builder().x(col).y(row).r(tempR).g(tempG).b(tempB).build();
                    ChartPoint result = matcher.getMatchHandler().apply(matchPoint);
                    double[] writeBackPoint = coverChartPointToRgbArray(result);
                    image.put(row, col, writeBackPoint);
                }
            }
        });
    }

    /**
     * 匹配两个点的坐标
     *
     * @param pointA 点A
     * @param pointB 点B
     * @return bool
     */
    public static Boolean matchXAndY(ChartPoint pointA, ChartPoint pointB) {
        String xyA = String.format("%s-%s", pointA.getX(), pointA.getY());
        String xyB = String.format("%s-%s", pointB.getX(), pointB.getY());
        return Objects.equals(xyA, xyB);
    }


    /**
     * 匹配图片中的点
     *
     * @param image   图片
     * @param matcher 匹配对象
     * @author 0neBean
     * @since 2022/11/26 15:30
     */
    public static void matchX(Mat image, ChartPointMatcher matcher) {
        eachMat(image, (point, row, col) -> {
            double tempB = point[0];
            double tempG = point[1];
            double tempR = point[2];
            List<ChartPoint> chartPoints = matcher.getChartPoints();
            for (ChartPoint chartPoint : chartPoints) {
                if (Objects.equals(col, chartPoint.getX())) {
                    ChartPoint matchPoint = ChartPoint.builder().x(col).y(row).r(tempR).g(tempG).b(tempB).build();
                    ChartPoint result = matcher.getMatchHandler().apply(matchPoint);
                    double[] writeBackPoint = coverChartPointToRgbArray(result);
                    image.put(row, col, writeBackPoint);
                }
            }
        });
    }

    /**
     * base64转Mat
     *
     * @param base64 图片
     * @return mat
     * @throws IOException io 异常
     */
    public static Mat base64ToMat(String base64) throws IOException {
        if (base64.startsWith(BASE64_PREFIX)) {
            base64 = base64.replace(BASE64_PREFIX, StringPool.EMPTY);
        }
        // 对base64进行解码
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] origin = decoder.decode(base64);
        InputStream in = new ByteArrayInputStream(origin); // 将b作为输入流；
        BufferedImage image = ImageIO.read(in);
        return BufImgToMat(image, BufferedImage.TYPE_3BYTE_BGR, CvType.CV_8UC3);
    }

    /**
     * BufferedImage转换成Mat
     *
     * @param original 要转换的BufferedImage
     * @param imgType  bufferedImage的类型 如 BufferedImage.TYPE_3BYTE_BGR
     * @param matType  转换成mat的type 如 CvType.CV_8UC3
     */
    public static Mat BufImgToMat(BufferedImage original, int imgType, int matType) throws IOException {
        Assert.notNull(original);
        if (original.getType() != imgType) {
            BufferedImage image = new BufferedImage(original.getWidth(), original.getHeight(), imgType);
            Graphics2D g = image.createGraphics();
            try {
                g.setComposite(AlphaComposite.Src);
                g.drawImage(original, 0, 0, null);
            } finally {
                g.dispose();
            }
        }
        Mat mat = new Mat(original.getHeight(), original.getWidth(), matType);
        for (int i = 0; i < original.getWidth(); i++) {
            for (int j = 0; j < original.getHeight(); j++) {
                int rgb = original.getRGB(i, j);
                Color color = new Color(rgb);
                double red = color.getRed();
                double blue = color.getBlue();
                double green = color.getGreen();
                double[] rgbArray = new double[3];
                rgbArray[0] = blue;
                rgbArray[1] = green;
                rgbArray[2] = red;
                mat.put(j, i, rgbArray);
            }
        }
        return mat;
    }

    /**
     * 匹配两个点的颜色
     *
     * @param pointA 点A
     * @param pointB 点B
     * @return bool
     */
    public static Boolean matchColor(ChartPoint pointA, ChartPoint pointB) {
        String rgbA = String.format("%s-%s-%s", pointA.getR(), pointA.getG(), pointA.getB());
        String rgbB = String.format("%s-%s-%s", pointB.getR(), pointB.getG(), pointB.getB());
        return Objects.equals(rgbA, rgbB);
    }


    /**
     * 获取一条线上最后的点的x值
     *
     * @param line 线
     * @return 最后一个点的x值
     */
    public static Integer getLastX(List<ChartPoint> line) {
        List<Integer> xList = line.stream().map(ChartPoint::getX).sorted(Comparator.comparing(Function.identity())).collect(Collectors.toList());
        return xList.get(xList.size() - 1);
    }

    /**
     * 按颜色获取线的下标
     *
     * @param topSpreadColorList 颜色分布排行
     * @param targetPoint        目标颜色点位
     * @return 下标
     */
    public static Integer getLineIndex(List<ChartPointColorCount> topSpreadColorList, ChartPoint targetPoint) {
        for (int i = 0; i < topSpreadColorList.size(); i++) {
            if (MatUtil.matchColor(targetPoint, topSpreadColorList.get(i).getChartPoint())) {
                return i;
            }
        }
        return -1;
    }

    public static void crossCovertPoint() {

    }

    /**
     * 包装点位对象成 rgb数组
     *
     * @param targetPoint 点位对象
     * @return rhb数组
     */
    private static double[] coverChartPointToRgbArray(ChartPoint targetPoint) {
        double[] ragArray = new double[3];
        ragArray[0] = targetPoint.getB();
        ragArray[1] = targetPoint.getG();
        ragArray[2] = targetPoint.getR();
        return ragArray;
    }

    /**
     * 构建坐标x-y 字符串
     *
     * @param x x坐标
     * @param y y坐标
     * @return x-y
     */
    private static String buildPointMapKey(Integer x, Integer y) {
        return String.format("%s-%s", x, y);
    }

}
