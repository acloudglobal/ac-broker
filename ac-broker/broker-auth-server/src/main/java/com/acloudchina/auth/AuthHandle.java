package com.acloudchina.auth;

public interface AuthHandle {

	ValidationInfo auth(AuthBean auth, int authType);
}
