package org.firefly.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor
public class EssayData {
    public String url;
    public String paragraph;
    public Integer totalTokens;
    public Integer totalValidWords;
}
