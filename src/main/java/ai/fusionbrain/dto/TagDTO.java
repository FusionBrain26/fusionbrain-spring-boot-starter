package ai.fusionbrain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TagDTO {
    @JsonProperty("name")
    private String name;

    @JsonProperty("name_en")
    private String nameEn;
}
