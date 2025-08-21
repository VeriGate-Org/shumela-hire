package com.example.recruitment.service;

import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

@Service
public class SlugGeneratorService {
    
    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");
    private static final Pattern EDGEHYPHENS = Pattern.compile("(^-|-$)");
    
    /**
     * Generate a URL-friendly slug from a title
     */
    public String generateSlug(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "job";
        }
        
        String nowhitespace = WHITESPACE.matcher(input).replaceAll("-");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        String slug = NONLATIN.matcher(normalized).replaceAll("");
        slug = EDGEHYPHENS.matcher(slug).replaceAll("");
        
        // Convert to lowercase and limit length
        slug = slug.toLowerCase(Locale.ENGLISH);
        if (slug.length() > 100) {
            slug = slug.substring(0, 100);
            slug = EDGEHYPHENS.matcher(slug).replaceAll("");
        }
        
        // Ensure we have a valid slug
        if (slug.isEmpty()) {
            slug = "job";
        }
        
        return slug;
    }
    
    /**
     * Generate a unique slug by appending numbers if necessary
     */
    public String generateUniqueSlug(String input, java.util.function.Function<String, Boolean> existsChecker) {
        String baseSlug = generateSlug(input);
        String slug = baseSlug;
        int counter = 1;
        
        while (existsChecker.apply(slug)) {
            slug = baseSlug + "-" + counter;
            counter++;
        }
        
        return slug;
    }
}