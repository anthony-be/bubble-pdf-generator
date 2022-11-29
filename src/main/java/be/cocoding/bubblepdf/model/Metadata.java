package be.cocoding.bubblepdf.model;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@Data
@Jacksonized
@Builder
public class Metadata {

    private String bucketId;

    private String pdfFileId;

    private List<String> labels;

    public boolean isCompleteForStorage(){
        return StringUtils.isNotBlank(bucketId)
                && StringUtils.isNotBlank(pdfFileId);
    }
}
