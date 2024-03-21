package com.crane.wordformat.formatter.factory;

import com.aspose.words.Body;
import com.aspose.words.Node;
import com.aspose.words.NodeCollection;
import com.aspose.words.NodeType;
import com.aspose.words.Paragraph;
import com.crane.wordformat.formatter.AbstractFormatter;
import com.crane.wordformat.formatter.AcademicCommentsFromInstructorsFormatter;
import com.crane.wordformat.formatter.AnnexFormatter;
import com.crane.wordformat.formatter.ChapterFormatter;
import com.crane.wordformat.formatter.DirectoryFormatter;
import com.crane.wordformat.formatter.EnAbstractFormatter;
import com.crane.wordformat.formatter.IllustrationScheduleChecklistFormatter;
import com.crane.wordformat.formatter.InstructionsDissertationAuthorizationFormatter;
import com.crane.wordformat.formatter.PublicReviewerDefenseCommitteeListsFormatter;
import com.crane.wordformat.formatter.ReferencesFormatter;
import com.crane.wordformat.formatter.ResolutionOfTheDefenceCommitteeFormatter;
import com.crane.wordformat.formatter.ResumeFormatter;
import com.crane.wordformat.formatter.StatementFormatter;
import com.crane.wordformat.formatter.SymbolAbbreviationsFormatter;
import com.crane.wordformat.formatter.ThanksFormatter;
import com.crane.wordformat.formatter.ZhAbstractFormatter;
import com.crane.wordformat.formatter.enums.SectionEnums;
import com.crane.wordformat.formatter.global.FormattingProcessShareVar;
import com.crane.wordformat.formatter.global.PostHandler;
import com.crane.wordformat.formatter.global.PreHandler;
import com.crane.wordformat.formatter.utils.StyleUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FormatterFactory {

  /*public static List<AbstractFormatter> formatters = new ArrayList<>() {
    {
      add(new AcademicCommentsFromInstructorsFormatter());
      add(new AnnexFormatter());
      add(new ChapterFormatter());
      add(new DirectoryFormatter());
      add(new EnAbstractFormatter());
      add(new InstructionsDissertationAuthorizationFormatter());
      add(new PublicReviewerDefenseCommitteeListsFormatter());
      add(new ReferencesFormatter());
      add(new ResolutionOfTheDefenceCommitteeFormatter());
      add(new ResumeFormatter());
      add(new StatementFormatter());
      add(new SymbolAbbreviationsFormatter());
      add(new ThanksFormatter());
      add(new ZhAbstractFormatter());
    }
  };

  static {
    checkFactory();
  }*/

  /**
   * <h2>严格遵守以下顺序：</h2>
   * 中文封面 <br/> 英文封面 <br/> 学位论文指导小组（如有）、公开评阅人和答辩委员会名单 <br/> 关于学位论文使用授权的说明 <br/> 摘要 <br/> Abstract
   * <br/> 目录 <br/> 插图和附表清单（如有） <br/> 符号和缩略语说明（如有） <br/> 正文：第 1 章（或引言），第 2 章，……，结论 <br/> 参考文献 <br/>
   * 附录（如有） <br/> 致谢 <br/> 声明 <br/> 个人简历、在学期间完成的相关学术成果 <br/> 指导教师（小组）学术评语 <br/> 答辩委员会决议书 <br/>
   * 其他材料（根据学位评定分委员会和院系要求提供） <br/>
   *
   * @param formattingProcessShareVar
   * @return
   */
  public static List<AbstractFormatter> create(
      FormattingProcessShareVar formattingProcessShareVar) {
    List<AbstractFormatter> formatters = new ArrayList<>() {
      {
        add(new PublicReviewerDefenseCommitteeListsFormatter(formattingProcessShareVar));
        add(new InstructionsDissertationAuthorizationFormatter(formattingProcessShareVar));
        add(new ZhAbstractFormatter(formattingProcessShareVar));
        add(new EnAbstractFormatter(formattingProcessShareVar));
        add(new DirectoryFormatter(formattingProcessShareVar));
        add(new IllustrationScheduleChecklistFormatter(formattingProcessShareVar));
        add(new SymbolAbbreviationsFormatter(formattingProcessShareVar));
        add(new ChapterFormatter(formattingProcessShareVar));
        add(new ReferencesFormatter(formattingProcessShareVar));
        add(new AnnexFormatter(formattingProcessShareVar));
        add(new ThanksFormatter(formattingProcessShareVar));
        add(new StatementFormatter(formattingProcessShareVar));
        add(new ResumeFormatter(formattingProcessShareVar));
        add(new AcademicCommentsFromInstructorsFormatter(formattingProcessShareVar));
        add(new ResolutionOfTheDefenceCommitteeFormatter(formattingProcessShareVar));
      }
    };
    checkFactory(formatters);
    return formatters;
  }

  private static void checkFactory(List<AbstractFormatter> formatters) {
    Map<SectionEnums, AbstractFormatter> map = new HashMap<>();
    for (AbstractFormatter formatter : formatters) {
      SectionEnums sectionEnums = formatter.sectionEnums();
      if (map.get(sectionEnums) != null) {
        throw new RuntimeException("唯一标识重复");
      }
      map.put(sectionEnums, formatter);
    }
  }

  public static void excute(FormattingProcessShareVar formattingProcessShareVar) throws Exception {
    List<AbstractFormatter> formatters = FormatterFactory.create(formattingProcessShareVar);
    new PreHandler(formattingProcessShareVar).execute();
    NodeCollection<Node> nodes = formattingProcessShareVar.getStudetDocument()
        .getChildNodes(NodeType.ANY, true);
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
    new PostHandler(formattingProcessShareVar, formatters).execute();
  }
}
