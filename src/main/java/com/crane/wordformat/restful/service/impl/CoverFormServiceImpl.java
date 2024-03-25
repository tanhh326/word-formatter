package com.crane.wordformat.restful.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.aspose.words.ImageSaveOptions;
import com.aspose.words.PageSet;
import com.aspose.words.SaveFormat;
import com.crane.wordformat.restful.service.CoverFormService;
import com.crane.wordformat.restful.utils.FilePathUtil;
import com.crane.wordformat.restful.utils.MinioClientUtil;
import io.minio.errors.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.aspose.words.Document;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class CoverFormServiceImpl implements CoverFormService {

    @Autowired
    MinioClientUtil minioClientUtil;

    @Override
    public List<String> wordConvertPNG(String CoverTemplateUrl) throws Exception {
        // 获取文件
        InputStream inputStream = minioClientUtil.getObject(CoverTemplateUrl);
        // 加载Word文档
        Document doc = new Document(inputStream);

        // 创建ImageSaveOptions对象，设置输出格式为PNG
        ImageSaveOptions options = new ImageSaveOptions(SaveFormat.PNG);
        // 设置分辨率
        options.setResolution(96); // DPI（每英寸点数），你可以根据需要调整这个值
        // 设置其他选项
        options.setPrettyFormat(true); // 设置是否以漂亮的格式呈现文档内容
        options.setUseAntiAliasing(true); // 设置是否使用抗锯齿，使图像边缘更平滑

        // 记录图片url
        List<String> coverPreviewUrl = new ArrayList<>();

        // 遍历Word文档中的每一页，将每一页转换为图片
        for (int pageIndex = 0; pageIndex < doc.getPageCount(); pageIndex++) {
            PageSet pageSet = new PageSet(pageIndex);
            options.setPageSet(pageSet);

            // 使用ByteArrayOutputStream来捕获输出
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            doc.save(out, options); // 将当前页转换为PNG图片并保存到ByteArrayOutputStream中
            String originalFilePath = FilePathUtil.build("png", "cover-Picture",
                    UUID.randomUUID().toString());
            minioClientUtil.putObject(originalFilePath,
                    new ByteArrayInputStream(out.toByteArray()));

            coverPreviewUrl.add(originalFilePath);
        }
        return coverPreviewUrl;
    }
}
