/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mindemia.imagegen.config;

/**
 *
 * @author rickard
 */
public record SamplerSettings(int seed, int steps, int startStep, int endStep, double denoise) {
    
}
