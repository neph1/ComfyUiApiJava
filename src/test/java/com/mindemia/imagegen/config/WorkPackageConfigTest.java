/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.mindemia.imagegen.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.IOException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author rickard
 */
public class WorkPackageConfigTest {
    
    public WorkPackageConfigTest() {
    }
    
    @BeforeAll
    public static void setUpClass() {
    }
    
    @AfterAll
    public static void tearDownClass() {
    }
    
    @BeforeEach
    public void setUp() {
    }
    
    @AfterEach
    public void tearDown() {
    }

    /**
     * Test of toString method, of class WorkPackageConfig.
     */
    @Test
    public void testLoad() {
        ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
        try {
            WorkPackageConfig config = yamlMapper.readValue(getClass().getClassLoader().getResourceAsStream("test_package_config.yml"), WorkPackageConfig.class);
        
            assertNotNull(config);
            assertEquals(512, config.settings().get("width"));
            assertEquals("cyberpunk.png", config.inputs().getFirst());
        } catch (IOException ex) {
            fail(ex.getMessage());
        }
    }

}
