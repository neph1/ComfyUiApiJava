/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mindemia.imagegen.response;

import java.util.List;
import java.util.Map;

/**
 *
 * @author rickard
 */
public record HistoryItem(List<Object> prompt, Map<String, Output> outputs, Map<String, Object> status, Map<String, Object> meta) {
    
}
