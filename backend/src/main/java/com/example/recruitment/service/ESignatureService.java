package com.example.recruitment.service;

import com.example.recruitment.entity.Offer;
import java.util.Map;

public interface ESignatureService {
    String sendForSignature(Offer offer, String signerEmail, String signerName);
    String getEnvelopeStatus(String envelopeId);
    byte[] getSignedDocument(String envelopeId);
    void handleWebhookEvent(Map<String, Object> event);
    void voidEnvelope(String envelopeId, String reason);
}
