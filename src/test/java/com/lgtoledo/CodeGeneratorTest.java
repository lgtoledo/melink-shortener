package com.lgtoledo;

import org.junit.jupiter.api.Test;

import com.lgtoledo.utils.CodeGenerator;

import static org.junit.jupiter.api.Assertions.*;

public class CodeGeneratorTest {
    
    @Test
    public void testGenerateCode() {
        String code = CodeGenerator.generateCode(6);
        assertNotNull(code);
        assertEquals(6, code.length());
    }

    @Test
    public void testGenerateCodeWithZeroLength() {
        String code = CodeGenerator.generateCode(0);
        assertNotNull(code);
        assertEquals("", code);
    }

}