package com.crane.wordformat.restful.dto;

import com.crane.wordformat.formatter.enums.DegreeEnums;
import lombok.Data;

@Data
public class FormatProcessDTO {

    private DegreeEnums degree;

    private String coverFormId;

    private String formatConfigId;
}
