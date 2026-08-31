package com.tkfc.core.toolkit;

    import com.tkfc.core.constants.StringPool;
    import lombok.extern.slf4j.Slf4j;
    import net.coobird.thumbnailator.Thumbnails;

    import javax.imageio.ImageIO;
    import javax.swing.*;
    import java.awt.*;
    import java.awt.geom.AffineTransform;
    import java.awt.image.BufferedImage;
    import java.io.File;
    import java.io.IOException;
    import java.math.BigDecimal;
    import java.math.RoundingMode;
    import java.io.FileInputStream;

/**
 * 图片
 *
 * @author 0neBean
 */
@Slf4j
public class ImgUtil {

    private final static long LIMIT_IMG_SIZE = 100000;
    private final static float LIMIT_IMG_HEIGHT_WIDTH = 800f;
    private final static String LIMIT_QUALITY = "0.75";

    /**
     * 添加全图水印
     * @param imagePath 图片路径
     * @param watermarkText 水印文本
     * @param filePathTypeName 路径中的文件类型名称
     * @throws IOException 异常
     */
    public static void addFullWatermark(String imagePath, String watermarkText, String filePathTypeName) throws IOException {
        BufferedImage originalImage = ImageIO.read(new File(imagePath));
        if (originalImage == null) {
            throw new IOException("无法读取图片文件: " + imagePath);
        }
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        Graphics2D g2d = (Graphics2D) originalImage.getGraphics();

        // 计算图片的平均亮度
        int totalBrightness = 0;
        int sampleSize = 100;
        for (int i = 0; i < sampleSize; i++) {
            int x = (int) (Math.random() * width);
            int y = (int) (Math.random() * height);
            Color pixelColor = new Color(originalImage.getRGB(x, y));
            totalBrightness += (pixelColor.getRed() + pixelColor.getGreen() + pixelColor.getBlue()) / 3;
        }
        int averageBrightness = totalBrightness / sampleSize;

        // 设置水印颜色
        Color watermarkColor = averageBrightness > 128 ? Color.BLACK : Color.WHITE;
        g2d.setColor(watermarkColor);
        g2d.setFont(new Font("Arial", Font.BOLD, 36));
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.15f));

        FontMetrics fontMetrics = g2d.getFontMetrics();
        int textWidth = fontMetrics.stringWidth(watermarkText);
        int textHeight = fontMetrics.getHeight();

        // 增加水印间距
        int spacingX = textWidth + 100; // 水平间距
        int spacingY = textHeight + 120;  // 垂直间距增加一倍

        // 计算旋转后的对角线长度
        double diagonal = Math.sqrt(width * width + height * height);

        // 计算需要的网格数量，确保覆盖整个图片
        int numX = (int) (diagonal / spacingX) + 7;  // 增加网格数量
        int numY = (int) (diagonal / spacingY) + 12;

        // 计算起始位置，确保从图片左上角开始
        int startX = -width;
        int startY = -height;

        // 旋转画布中心
        AffineTransform originalTransform = g2d.getTransform();
        g2d.rotate(Math.toRadians(-45), width / 2.0, height / 2.0);

        for (int i = 0; i < numX; ++i) {
            for (int j = 0; j < numY; ++j) {
                // 错开每行的起始位置
                int x = startX + i * spacingX + (j % 2) * (spacingX / 2);
                int y = startY + j * spacingY;
                g2d.drawString(watermarkText, x, y);
            }
        }

        g2d.setTransform(originalTransform); // 恢复变换
        g2d.dispose();

        // 去掉开头点
        filePathTypeName = filePathTypeName.startsWith(".") ? filePathTypeName.substring(1) : filePathTypeName;
        ImageIO.write(originalImage, filePathTypeName, new File(imagePath));
    }

    /**
     * 改变图片分辨率
     *
     * @param fromPic 原图片
     * @param toPic   目标的图片文件
     * @param width   宽度
     * @param height  高度
     * @throws IOException 异常
     */
    public static void changeImgSize(File fromPic, File toPic, Integer width, Integer height) throws IOException {
        Thumbnails.of(fromPic).size(width, height).toFile(toPic);
    }

    /**
     * 以图片中心点裁切指定宽高的图片
     *
     * @param fromPic 原图片
     * @param toPic   目标的图片文件
     * @param width   裁切宽度
     * @param height  裁切高度
     * @throws IOException 异常
     */
    public static void changeImgSizeWithCenterCrop(File fromPic, File toPic, int width, int height) throws IOException {
        BufferedImage originalImage = ImageIO.read(fromPic);
        if (originalImage == null) {
            throw new IOException("无法读取原图片");
        }

        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();

        // 计算裁切区域的起始坐标
        int x = (originalWidth - width) / 2;
        int y = (originalHeight - height) / 2;

        // 确保裁切区域不超出原图范围
        x = Math.max(0, x);
        y = Math.max(0, y);
        width = Math.min(width, originalWidth - x);
        height = Math.min(height, originalHeight - y);

        // 创建裁切后的图片
        BufferedImage croppedImage = originalImage.getSubimage(x, y, width, height);

        // 保存裁切后的图片
        String formatName = getFileExtendName(IoUtil.readStreamAsByteArray(new FileInputStream(fromPic))).toLowerCase();
        ImageIO.write(croppedImage, formatName, toPic);
    }

    /**
     * 按比例缩小图片
     *
     * @param fromPic    原图片
     * @param toPic      目标的图片文件
     * @param percentage 百分比
     * @throws IOException 异常
     */
    public static void smallerImgSize(File fromPic, File toPic, Float percentage) throws IOException {
        Thumbnails.of(fromPic).scale(percentage).toFile(toPic);
    }

    /**
     * 按比例放大图片
     *
     * @param fromPic    原图片
     * @param toPic      目标的图片文件
     * @param percentage 百分比
     * @throws IOException 异常
     */
    public static void biggerImgSize(File fromPic, File toPic, Float percentage) throws IOException {
        Thumbnails.of(fromPic).scale(percentage).toFile(toPic);
    }

    /**
     * 按比例压缩图片
     *
     * @param fromPic           原图片
     * @param toPic             目标的图片文件
     * @param sizePercentage    尺寸百分比
     * @param qualityPercentage 质量百分比
     * @throws IOException 异常
     */
    public static void tarImg(File fromPic, File toPic, Float sizePercentage, Float qualityPercentage) throws IOException {
        Thumbnails.of(fromPic).scale(sizePercentage).outputQuality(qualityPercentage).toFile(toPic);
    }


    /**
     * 自动压缩图片
     *
     * @param fromPic 原图片
     * @param toPic   目标的图片文件
     * @throws IOException 异常
     */
    public static void tarImg(File fromPic, File toPic) throws IOException {
        Thumbnails.of(fromPic).scale(getAutoWidthHeight(fromPic)).outputQuality(getAutoQuality(fromPic)).toFile(toPic);
    }

    /**
     * webp 转换成 png
     *
     * @param fromPic 原图片
     * @param toPic   目标图标
     * @throws IOException io异常
     */
    public static void webpToPng(File fromPic, File toPic) throws IOException {
        Thumbnails.of(fromPic)
                .scale(1)
                .outputFormat("png")
                .toFile(toPic);
    }

    /**
     * 将任意静态图片格式转换为 WebP 格式
     *
     * @param fromPic 原图片文件（支持 JPG、PNG、GIF、BMP、WebP 等格式）
     * @param toPic   目标 WebP 文件
     * @throws IOException 如果读取或写入图片时发生错误
     */
    public static void toWebp(File fromPic, File toPic) throws IOException {
        Thumbnails.of(fromPic)
                .scale(1)
                .outputFormat("webp")
                .toFile(toPic);
    }

    /**
     * 判断文件是否需要转换为 WebP 格式
     *
     * @param filePath 文件路径
     * @return true 表示需要转换，false 表示不需要转换（已经是 WebP 或不是支持的图片格式）
     */
    public static boolean needConvertToWebp(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return false;
        }

        // 获取文件扩展名
        String extension = IoUtil.getFilePathTypeName(filePath);
        if (extension == null || extension.isEmpty()) {
            return false;
        }

        // 转换为小写进行比较
        String ext = extension.toLowerCase();

        // 如果已经是 WebP 格式，不需要转换
        if (ext.equals(".webp") || ext.equals("webp")) {
            return false;
        }

        // 如果已经是 WebP 格式，不需要转换
        if (ext.equals(".avif") || ext.equals("avif")) {
            return false;
        }

        // 支持的图片格式：JPG、JPEG、PNG、GIF、BMP
        return ext.equals(".jpg") || ext.equals("jpg") ||
               ext.equals(".jpeg") || ext.equals("jpeg") ||
               ext.equals(".png") || ext.equals("png") ||
               ext.equals(".gif") || ext.equals("gif") ||
               ext.equals(".bmp") || ext.equals("bmp");
    }

    /**
     * 自动计算质量
     *
     * @param fromPic 需要加工的图片
     * @return 质量
     */
    public static Float getAutoQuality(File fromPic) {
        long fSize = fromPic.length();
        BigDecimal b1 = new BigDecimal(Long.toString(fSize));
        BigDecimal b2 = new BigDecimal(LIMIT_IMG_SIZE + "");
        BigDecimal limit = new BigDecimal(LIMIT_QUALITY);

        if (NumberUtil.aDYb(b1, b2)) {
            BigDecimal divide = b2.divide(b1, RoundingMode.HALF_UP);
            divide = (NumberUtil.aXYb(divide, limit)) ? limit : divide;
            return ParseUtil.toFloat(divide);
        }
        return 1f;
    }

    /**
     * 自动计算尺寸
     *
     * @param fromPic 需要加工的图片
     * @return 尺寸
     */
    public static Float getAutoWidthHeight(File fromPic) {
        Integer imgWidth = getImgWidth(fromPic.getAbsolutePath());
        Integer imHeight = getImHeight(fromPic.getAbsolutePath());
        Integer fSize = (imgWidth > imHeight) ? imgWidth : imHeight;

        BigDecimal b1 = new BigDecimal(fSize.toString());
        BigDecimal b2 = new BigDecimal(LIMIT_IMG_HEIGHT_WIDTH + "");

        if (NumberUtil.aDYb(b1, b2)) {
            return ParseUtil.toFloat(b2.divide(b1, RoundingMode.HALF_UP));
        }
        return 1f;
    }

    /**
     * 获取图片宽度
     *
     * @param path 图片路径
     * @return 宽度
     */
    public static Integer getImgWidth(String path) {
        ImageIcon imageIcon = new ImageIcon(path);
        return imageIcon.getIconWidth();
    }

    /**
     * 获取图片长度
     *
     * @param path 图片路径
     * @return 长度
     */
    public static Integer getImHeight(String path) {
        ImageIcon imageIcon = new ImageIcon(path);
        return imageIcon.getIconHeight();
    }

    /**
     * 获取文件类型
     *
     * @param photoByte 文件字节码
     * @return 后缀（不含".")
     */
    public static String getFileExtendName(byte[] photoByte) {
        String strFileExtendName = "JPG";
        if ((photoByte[0] == 71) && (photoByte[1] == 73) && (photoByte[2] == 70) && (photoByte[3] == 56)
                && ((photoByte[4] == 55) || (photoByte[4] == 57)) && (photoByte[5] == 97)) {
            strFileExtendName = "GIF";
        } else if ((photoByte[6] == 74) && (photoByte[7] == 70) && (photoByte[8] == 73) && (photoByte[9] == 70)) {
            strFileExtendName = "JPG";
        } else if ((photoByte[0] == 66) && (photoByte[1] == 77)) {
            strFileExtendName = "BMP";
        } else if ((photoByte[1] == 80) && (photoByte[2] == 78) && (photoByte[3] == 71)) {
            strFileExtendName = "PNG";
        }
        return strFileExtendName;
    }

    /**
     * 向指定路径的图片添加文字水印。
     * 该方法读取给定路径的图片，在图片上绘制一个矩形背景，并在其中居中显示提供的水印文本，然后将修改后的图片保存回原路径。
     *
     * @param imagePath 图片文件的绝对路径
     * @param filePathTypeName 文件类型名称，例如 "jpg" 或 "png"
     * @param watermark 要添加到图片上的水印文本
     * @throws IOException 如果读取或写入图片时发生错误
     */
    public static void addWatermarkTips(String imagePath, String filePathTypeName, String watermark) throws IOException {
        BufferedImage srcImg = ImageIO.read(new File(imagePath));
        int imgW = srcImg.getWidth();
        int imgH = srcImg.getHeight();

        // 判断图片格式，jpg 用 TYPE_INT_RGB，其他用 TYPE_INT_ARGB
        int imageType = "png".equalsIgnoreCase(filePathTypeName.replace(".", "")) ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB;
        BufferedImage outImg = new BufferedImage(imgW, imgH, imageType);
        Graphics2D g2d = outImg.createGraphics();
        g2d.drawImage(srcImg, 0, 0, null);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int fontSize = Math.max(imgW, imgH) / 25;
        Font font = new Font("Arial", Font.BOLD, fontSize);
        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics();
        int textW = fm.stringWidth(watermark);
        int textH = fm.getHeight();

        int padding = fontSize / 3;
        int rectW = textW + padding * 2;
        int rectH = textH + padding;
        int arc = fontSize / 2;

        int x = imgW / 2 + imgW / 10 - rectW / 2;
        int y = imgH / 2 + imgH / 10 - rectH / 2;

        // 背景矩形（不透明，便于调试）
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        g2d.setColor(new Color(20, 20, 20, 255)); // 不透明深灰
        g2d.fillRoundRect(x, y, rectW, rectH, arc, arc);

        // 文字（白色高对比）并居中
        g2d.setColor(Color.WHITE);
        // 水平居中
        int textX = x + (rectW - textW) / 2;
        // 垂直居中
        int textY = y + (rectH - textH) / 2 + fm.getAscent();
        g2d.drawString(watermark, textX, textY);

        g2d.dispose();

        // 另存为新文件，避免缓存
        filePathTypeName = filePathTypeName.startsWith(".") ? filePathTypeName.substring(1) : filePathTypeName;
        ImageIO.write(outImg, filePathTypeName, new File(imagePath));
        System.out.println("水印图片已保存到: " + imagePath);
    }
}
