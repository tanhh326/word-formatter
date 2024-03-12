package com.crane.wordformat.restful.controller;

import com.aspose.words.Body;
import com.aspose.words.Document;
import com.aspose.words.Node;
import com.aspose.words.NodeCollection;
import com.aspose.words.NodeType;
import com.aspose.words.Paragraph;
import com.aspose.words.SaveFormat;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.crane.wordformat.formatter.AbstractFormatter;
import com.crane.wordformat.formatter.dto.StyleConfigDto;
import com.crane.wordformat.formatter.factory.FormatterFactory;
import com.crane.wordformat.formatter.global.FormattingProcessShareVar;
import com.crane.wordformat.formatter.global.PostHandler;
import com.crane.wordformat.formatter.global.PreHandler;
import com.crane.wordformat.formatter.utils.StyleUtils;
import com.crane.wordformat.restful.entity.FormatConfigPO;
import com.crane.wordformat.restful.mapper.FormatConfigMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/word-format")
public class IndexController {

    @Autowired
    private FormatConfigMapper formatConfigMapper;

    @PostMapping("/formatting")
    public ResponseEntity<byte[]> upload(@RequestParam("file") MultipartFile multipartFile)
            throws Exception {
        FormatConfigPO formatConfigPO = formatConfigMapper.selectList(new QueryWrapper<>()).get(0);
        List<StyleConfigDto> styleConfigDtos = new ObjectMapper().convertValue(formatConfigPO.getConfig(), new TypeReference<>() {
        });
        System.out.println("start" + System.currentTimeMillis());
        Document studentDocument = new Document(multipartFile.getInputStream());
        FormattingProcessShareVar formattingProcessShareVar = new FormattingProcessShareVar(
                styleConfigDtos, studentDocument);
        List<AbstractFormatter> formatters = FormatterFactory.create(formattingProcessShareVar);
        new PreHandler(studentDocument).execute();
        NodeCollection<Node> nodes = studentDocument.getChildNodes(NodeType.ANY, true);
        Node[] nodesArray = nodes.toArray();
        for (Node node : nodesArray) {
            // 只有在正文中的段落，并且不是toc目录中的才会被确认标题
            if (node instanceof Paragraph paragraph &&
                    paragraph.getParentNode() instanceof Body &&
                    StyleUtils.isNotToc(paragraph)
            ) {
                for (AbstractFormatter formatter : formatters) {
                    formatter.confirmTitle(paragraph);
                }
            }
        }
        for (AbstractFormatter formatter : formatters) {
            formatter.formatTitle();
        }
        for (AbstractFormatter formatter : formatters) {
            formatter.formatBody();
        }
        new PostHandler(studentDocument, formatters).execute();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        studentDocument.save(byteArrayOutputStream, SaveFormat.DOCX);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "result.docx");
        System.out.println("end" + System.currentTimeMillis());
        return ResponseEntity.ok(byteArrayOutputStream.toByteArray());
    }

    @GetMapping("/beat")
    public String beat() {
        return UUID.randomUUID().toString();
    }
}
