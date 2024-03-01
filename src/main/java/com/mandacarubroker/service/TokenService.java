package com.mandacarubroker.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.mandacarubroker.domain.auth.ResponseAuthUserDTO;
import com.mandacarubroker.security.SecuritySecrets;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class TokenService {
    private static final String TOKEN_ISSUER = "mandacaru-broker";
    private static final int EXPIRATION_TIME = 864 * 1000 * 1000;
    private static final int EXPIRATION_TIME_IN_SECONDS = EXPIRATION_TIME / 1000;
    private static final String TOKEN_TYPE = "Bearer";
    private final Algorithm algorithm;

    public TokenService() {
        String secret = SecuritySecrets.getJWTSecret();
        algorithm = Algorithm.HMAC512(secret.getBytes());
    }

    public ResponseAuthUserDTO encodeToken(final String subject) {
        try {
            return tryToEncodeToken(subject);
        } catch (JWTCreationException exception) {
            return null;
        }
    }

    private ResponseAuthUserDTO tryToEncodeToken(final String subject) {
        Date expirationDate = new Date(System.currentTimeMillis() + EXPIRATION_TIME);

        String generatedToken = JWT.create()
                .withSubject(subject)
                .withIssuer(TOKEN_ISSUER)
                .withExpiresAt(expirationDate)
                .sign(algorithm);

        String tokenWithPrefix = TOKEN_TYPE + " " + generatedToken;
        return new ResponseAuthUserDTO(tokenWithPrefix, EXPIRATION_TIME_IN_SECONDS, TOKEN_TYPE);
    }

    public String getTokenSubject(final String token) {
        DecodedJWT decodedToken = decodeUserToken(token);
        return decodedToken.getSubject();
    }

    public DecodedJWT decodeUserToken(final String token) {
        try {
            return tryToDecodeUserToken(token);
        } catch (Exception exception) {
            return null;
        }
    }

    private DecodedJWT tryToDecodeUserToken(final String token) {
        return JWT.require(algorithm).build().verify(token);
    }
}
