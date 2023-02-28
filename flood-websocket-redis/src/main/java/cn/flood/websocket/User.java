package cn.flood.websocket;

import java.io.Serializable;
import lombok.Data;

@Data
public class User implements Serializable {

  private String ownAccountId;
  private String defaultDeptId;
  private String headPic;
  private String account;
  private String userName;
  private String gender;
  private String phoneNumber;
  private String deptName;

  private String role;

  private String roleCodes;
  private String permissionCodes;

}
