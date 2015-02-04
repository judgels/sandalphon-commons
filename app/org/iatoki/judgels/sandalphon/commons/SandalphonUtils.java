package org.iatoki.judgels.sandalphon.commons;

import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import play.Logger;
import play.Play;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public final class SandalphonUtils {

    public static String getClientId() {
        String clientId = Play.application().configuration().getString("sandalphon.clientId");
        if (clientId == null) {
            throw new IllegalStateException("sandalphon.clientId not found in configuration");
        }
        return clientId;
    }

    public static String getClientSecret() {
        String clientSecret = Play.application().configuration().getString("sandalphon.clientSecret");
        if (clientSecret == null) {
            throw new IllegalStateException("sandalphon.clientSecret not found in configuration");
        }
        return clientSecret;
    }

    public static boolean verifyProblemJid(String problemJid) {
        HTTPRequest httpRequest;
        try {
            httpRequest = new HTTPRequest(HTTPRequest.Method.GET, getEndpoint("verifyProblem").toURL());
            httpRequest.setQuery("clientId=" + getClientId() + "&clientSecret=" + getClientSecret() + "&problemJid=" + problemJid);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        System.out.println(httpRequest.getQuery());

        try {
            HTTPResponse httpResponse = httpRequest.send();
            if (httpResponse.getStatusCode() == HTTPResponse.SC_OK) {
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static int calculateTOTPCode(String keyString, long tm) {
        long totpMod = 1000000;
        long timeStep = 30000;

        byte[] key = keyString.getBytes();
        byte[] data = new byte[8];
        long value = tm / timeStep;

        for(int signKey = 8; signKey-- > 0; value >>>= 8) {
            data[signKey] = (byte)((int)value);
        }

        SecretKeySpec var15 = new SecretKeySpec(key, "HmacSHA1");

        try {
            Mac ex = Mac.getInstance("HmacSHA1");
            ex.init(var15);
            byte[] hash = ex.doFinal(data);
            int offset = hash[hash.length - 1] & 15;
            long truncatedHash = 0L;

            for(int i = 0; i < 4; ++i) {
                truncatedHash <<= 8;
                truncatedHash |= (long)(hash[offset + i] & 255);
            }

            truncatedHash &= 2147483647L;
            truncatedHash %= totpMod;
            return (int)truncatedHash;
        } catch (InvalidKeyException | NoSuchAlgorithmException var14) {
            Logger.warn(var14.getMessage(), var14);
            throw new RuntimeException("The operation cannot be performed now.");
        }
    }

    public static URI getTOTPEndpoint(String problemJid, int tOTP, String lang) {
        return getEndpoint("totp/" + getClientId() + "/" + problemJid + "/statement/" + tOTP + "/" + lang);
    }

    public static URI getEndpoint(String service) {
        String baseUrl = Play.application().configuration().getString("sandalphon.baseUrl");
        if (baseUrl == null) {
            throw new IllegalStateException("sandalphon.baseUrl not found in configuration");
        }

        try {
            return new URI(baseUrl + "/" + service);
        } catch (URISyntaxException e) {
            throw new IllegalStateException("sandalphon.baseUrl malformed in configuration");
        }
    }
}
