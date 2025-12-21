package com.mindemia.imagegen.response;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;


@JsonIgnoreProperties(ignoreUnknown = true)
public record Output(List<ResultFile> images, List<ResultFile> gifs, List<ResultFile> latents) {
    
}
