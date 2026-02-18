package com.arthmatic.shumelahire.entity;

public enum QuestionType {
    TEXT,           // Free text input
    DROPDOWN,       // Single selection from options
    YES_NO,         // Boolean yes/no question
    MULTIPLE_CHOICE,// Single selection from multiple options
    CHECKBOX,       // Multiple selections allowed
    NUMBER,         // Numeric input
    DATE,           // Date picker
    EMAIL,          // Email validation
    PHONE,          // Phone number input
    FILE_UPLOAD     // File attachment
}