package org.iatoki.judgels.sandalphon.commons;

import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import play.Logger;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public final class Sandalphon implements BundleProblemGrader {
    private final String clientJid;
    private final String clientSecret;
    private final String baseUrl;

    public Sandalphon(String clientJid, String clientSecret, String baseUrl) {
        this.clientJid = clientJid;
        this.clientSecret = clientSecret;
        this.baseUrl = baseUrl;
    }

    public String verifyLessonJid(String lessonJid) {
        HTTPRequest httpRequest;
        try {
            httpRequest = new HTTPRequest(HTTPRequest.Method.GET, getEndpoint("verifyLesson").toURL());
            httpRequest.setQuery("clientJid=" + clientJid + "&clientSecret=" + clientSecret + "&lessonJid=" + lessonJid);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        try {
            HTTPResponse httpResponse = httpRequest.send();
            if (httpResponse.getStatusCode() == HTTPResponse.SC_OK) {
                return httpResponse.getContent();
            } else {
                return null;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public URI getLessonRenderUri(String lessonJid, String imageName) {
        try {
            return getEndpoint("lessons/" + lessonJid + "/render/" + imageName).toURL().toURI();
        } catch (MalformedURLException | URISyntaxException e) {
            return null;
        }
    }

    public String verifyProblemJid(String problemJid) {
        HTTPRequest httpRequest;
        try {
            httpRequest = new HTTPRequest(HTTPRequest.Method.GET, getEndpoint("verifyProblem").toURL());
            httpRequest.setQuery("clientJid=" + clientJid + "&clientSecret=" + clientSecret + "&problemJid=" + problemJid);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        try {
            HTTPResponse httpResponse = httpRequest.send();
            if (httpResponse.getStatusCode() == HTTPResponse.SC_OK) {
                return httpResponse.getContent();
            } else {
                return null;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public URI getProblemRenderUri(String problemJid, String imageName) {
        try {
            return getEndpoint("problems/" + problemJid + "/render/" + imageName).toURL().toURI();
        } catch (MalformedURLException | URISyntaxException e) {
            return null;
        }
    }

    @Override
    public BundleGradingResult gradeBundleProblem(String problemJid, BundleAnswer bundleAnswer) throws IOException {
        return null;
    }

    public int calculateTOTPCode(String keyString, long tm) {
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

    public URI getLessonTOTPEndpoint(String lessonJid, int tOTP, String lang, String switchLanguageUri) {
        return getEndpoint("lesson/totp/" + clientJid + "/" + lessonJid + "/statement/" + tOTP + "/" + lang + "/" + URLEncoder.encode(switchLanguageUri));
    }

    public URI getProblemTOTPEndpoint(String problemJid, int tOTP, String lang, String postSubmitUri, String switchLanguageUri) {
        return getEndpoint("problem/totp/" + clientJid + "/" + problemJid + "/statement/" + tOTP + "/" + lang + "/" + URLEncoder.encode(postSubmitUri) + "/" + URLEncoder.encode(switchLanguageUri));
    }

    public URI getEndpoint(String service) {
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
