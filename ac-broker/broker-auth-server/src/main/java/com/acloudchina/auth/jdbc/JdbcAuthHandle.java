package com.acloudchina.auth.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.acloudchina.auth.AuthBean;
import com.acloudchina.auth.AuthConstants;
import com.acloudchina.auth.AuthHandle;
import com.acloudchina.auth.ValidationInfo;

@Service("authHandle")
public class JdbcAuthHandle implements AuthHandle {

	@Autowired
	private AuthDao authDao;

	@Override
	public ValidationInfo auth(AuthBean auth, int authType) {
		ValidationInfo info = null;
		boolean result = false;
		switch (authType) {
		case AuthConstants.AUTH_NO:
			info = returnReply(true, auth.getUsername(), "认证成功");
			break;
		case AuthConstants.AUTH_CLIENTID:
			result = authDao.auth(auth.getEndpointId(), auth.getTenantId());
			if (result) {
				info = returnReply(true, auth.getUsername(), "认证成功");
			} else {
				info = returnReply(false, auth.getUsername(), "认证失败");
			}

			break;
		case AuthConstants.AUTH_CLIENTID_USERNAME_PASSWORD:
			result = authDao.auth(auth.getEndpointId(), auth.getTenantId(), auth.getUsername(), auth.getPassword());
			if (result) {
				info = returnReply(true, auth.getUsername(), "认证成功");
			} else {
				info = returnReply(false, auth.getUsername(), "认证失败");
			}
			break;
		case AuthConstants.AUTH_TOKEN:
			result = authDao.authToken(auth.getToken());
			if (result) {
				info = returnReply(true, auth.getUsername(), "认证成功");
			} else {
				info = returnReply(false, auth.getUsername(), "认证失败");
			}

			break;
		default:
			System.out.println("没有配置认证方式，或者配置错误");
			System.out.println("走认证链");
			//暂不支持
			info = returnReply(false, auth.getUsername(), "认证失败");
			break;
		}
		return info;
	}

	private ValidationInfo returnReply(boolean result, String username, String msg) {
		ValidationInfo vi = new ValidationInfo();
		vi.auth_valid = result;
		vi.authorized_user = username;
		vi.error_msg = msg;

		return vi;
	}

}
