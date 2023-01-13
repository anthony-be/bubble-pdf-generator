package be.cocoding.bubblepdf.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

@Data
@AllArgsConstructor
@Jacksonized
@Builder
public class ImageElement implements Element {

    public static final String TYPE = "image";

    private static final Logger logger = LoggerFactory.getLogger(ImageElement.class);

    @JsonProperty("value")
    private String value;

    private File mirrorLocalFile;

    public ImageElement(String value) {
        this.value = value;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    public boolean isBase64Value() {
        return Base64.isBase64(value);
    }

    @Override
    public String getValue() {
        return value;
    }

    public void setMirrorLocalFile(File mirrorLocalFile) {
        this.mirrorLocalFile = mirrorLocalFile;
    }

    public byte[] getImageBytes() {
        byte[] bytes;
        if(mirrorLocalFile != null){
            try {
                bytes = FileUtils.readFileToByteArray(mirrorLocalFile);
            } catch (IOException e) {
                throw new RuntimeException("Failed to read mirrored local file",e);
            }
        } else if (isBase64Value()) {
            bytes = java.util.Base64.getDecoder().decode(getValue());
        } else {
            throw new IllegalStateException("ImageElement is not a base64 representation and has not been previously downloaded");
        }
        return bytes;
    }
}
