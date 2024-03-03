package com.mandacarubroker.domain.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Permission {
    STOCKS_READ("stocks:read"),
    STOCKS_CREATE("stocks:create"),
    STOCKS_UPDATE("stocks:update"),
    STOCKS_DELETE("stocks:delete"),
    USER_CREATE("user:create"),
    USER_READ("user:read"),
    USER_UPDATE("user:update"),
    USER_DELETE("user:delete");

    @Getter
    private final String permission;
}
