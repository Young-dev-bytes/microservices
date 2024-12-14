package com.in28minutes.microservices.mlagenteval.common.event;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class JobTrackDetailEvent {

    private String trackId;

    private List<String> respInfers;

    private String step;


}
