package com.arthmatic.shumelahire.service;

import com.arthmatic.shumelahire.entity.Offer;
import java.util.Map;

public interface ESignatureService {
    String sendForSignature(Offer offer, String signerEmail, String signerName);
    String getEnvelopeStatus(String envelopeId);
    byte[] getSignedDocument(String envelopeId);
    void handleWebhookEvent(Map<String, Object> event);
    void voidEnvelope(String envelopeId, String reason);
}
