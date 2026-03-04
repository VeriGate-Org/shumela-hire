package com.arthmatic.shumelahire.service.ai;

public class AiCompletionResponse {

    private String content;
    private String model;
    private int inputTokens;
    private int outputTokens;
    private String provider;

    public AiCompletionResponse() {}

    public AiCompletionResponse(String content, String model, int inputTokens, int outputTokens, String provider) {
        this.content = content;
        this.model = model;
        this.inputTokens = inputTokens;
        this.outputTokens = outputTokens;
        this.provider = provider;
    }

    public String getContent() { return content; }

    /**
     * Returns the content with markdown code fences stripped, suitable for JSON parsing.
     * LLMs often wrap JSON responses in ```json ... ``` blocks.
     */
    public String getJsonContent() {
        if (content == null) return null;
        String trimmed = content.trim();
        if (trimmed.startsWith("```")) {
            // Strip opening fence (with optional language tag) and closing fence
            int firstNewline = trimmed.indexOf('\n');
            if (firstNewline >= 0) {
                trimmed = trimmed.substring(firstNewline + 1);
            }
            if (trimmed.endsWith("```")) {
                trimmed = trimmed.substring(0, trimmed.length() - 3).trim();
            }
        }
        return trimmed;
    }
    public void setContent(String content) { this.content = content; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public int getInputTokens() { return inputTokens; }
    public void setInputTokens(int inputTokens) { this.inputTokens = inputTokens; }

    public int getOutputTokens() { return outputTokens; }
    public void setOutputTokens(int outputTokens) { this.outputTokens = outputTokens; }

    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }
}
