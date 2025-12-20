package com.mindemia.imagegen.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ResultFile(String filename, String subfolder, String type, String format) {

}
