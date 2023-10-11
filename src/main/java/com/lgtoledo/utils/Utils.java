package com.lgtoledo.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    /**
     * Valida si una URL cumple con el formato dado por una expresión regular.
     * @param optUrl La URL a validar.
     * @param regex La expresión regular contra la cual validar la URL.
     * @return true si la URL es válida, false en caso contrario.
     */
    public static boolean isValidUrl(Optional<String> optUrl, String regex) {
        if (!optUrl.isPresent()) {
            return false;
        }
        String url = optUrl.get();
        
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(url);
        return matcher.matches();
    }

    /**
     * Genera un link corto aleatorio de 6 caracteres.
     * @return Un identificador único aleatorio para el link corto.
     */
    public static String generateShortLink() {
        return UUID.randomUUID().toString().substring(0, 6);
    }

    /**
     * Extrae la última parte de un link corto(ID)
     * @param shortUrl El link corto del cual extraer la última parte.
     * @return La última parte del link corto.
     */
    public static String extractLastPart(String shortUrl) {
        String[] parts = shortUrl.split("/");
        return parts[parts.length - 1];
    }
    /*
     * Retorna la fecha y hora actual en UTC.
     */
    public static LocalDateTime getCurrentUtcDateTime() {
        ZonedDateTime utc = ZonedDateTime.now(ZoneId.of("UTC"));
        return utc.toLocalDateTime();
    }


}
