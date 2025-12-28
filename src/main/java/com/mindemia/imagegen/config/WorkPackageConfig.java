/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mindemia.imagegen.config;

import com.mindemia.imagegen.workpackage.BaseWorkPackage.OutputSpec;
import java.util.List;
import java.util.Map;

/**
 *
 * @author rickard
 */
public record WorkPackageConfig(String name, String workflow, List<String> inputs, List<OutputSpec> outputs, Map<String, Object> settings) {
    
}
