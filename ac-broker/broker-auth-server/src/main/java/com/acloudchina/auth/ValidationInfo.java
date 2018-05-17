package com.acloudchina.auth;

import lombok.Data;

@Data
public class ValidationInfo {
    public Boolean auth_valid;
    public String authorized_user;
    public String error_msg;
    public String tenant;
    public String token = null;
}
