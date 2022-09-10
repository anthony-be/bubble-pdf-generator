package be.cocoding.bubblepdf.model;

import lombok.*;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Data
@Jacksonized
@Builder
public class Question {

    private String title;

    private List<Element> elements;

}

