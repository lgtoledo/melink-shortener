package com.lgtoledo.utils;

import java.security.SecureRandom;

/**
 * Genera un código alfanumérico aleatorio de longitud especificada. 
 * <p>
 * El código generado se compone de letras mayúsculas, letras minúsculas y números.
 * Utiliza SecureRandom para garantizar que los números aleatorios generados sean adecuados para usos de seguridad.
 * </p>
 *
 * @param length La longitud del código que se desea generar.
 * @return Un String que representa el código alfanumérico aleatorio de la longitud especificada.
 **/
public class CodeGenerator {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generateCode(int length) {
        StringBuilder result = new StringBuilder(length);
        
        for (int i = 0; i < length; i++) {
            result.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        
        return result.toString();
    }

}