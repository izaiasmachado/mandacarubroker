package com.mandacarubroker.security;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

public final class SecuritySecretsMock {
    private static MockedStatic<SecuritySecrets> securitySecretsMockedStatic = null;

    private SecuritySecretsMock() {
    }

    public static void mockStatic() {
        if (securitySecretsMockedStatic != null) {
            return;
        }

        securitySecretsMockedStatic = Mockito.mockStatic(SecuritySecrets.class);
        securitySecretsMockedStatic.when(SecuritySecrets::getJWTSecret).thenReturn("secret");
    }
}
