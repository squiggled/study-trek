package vttp.proj2.backend.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import vttp.proj2.backend.models.AccountInfo;

@Repository
public class AuthRepository {

    @Autowired
	private JdbcTemplate template;

    public boolean createNewUser(AccountInfo userInfo){
        return template.update(Queries.SQL_CREATE_NEW_USER, userInfo.getUserId(), userInfo.getEmail(), userInfo.getPasswordHash(), userInfo.getLastPasswordResetDate(), userInfo.getFirstName(), userInfo.getLastName(), userInfo.getProfilePicUrl()) > 0;
    }
    
    public boolean assignUserRole(String userId, String roleName){
        return template.update(Queries.SQL_ASSIGN_ROLE_USER, userId, roleName) > 0;
    }

    public boolean checkEmailExists(String email){
        SqlRowSet rs = template.queryForRowSet(Queries.SQL_FIND_USER_BY_EMAIL, email);
        if (rs.next()) return true;
        return false;
    }

    public String getHashedPassword(String email){
        SqlRowSet rs = template.queryForRowSet(Queries.SQL_GET_HASHED_PASSWORD_BY_EMAIL, email);
        if (rs.next()) return rs.getString("passwordHash");
		return null;
    }

    public AccountInfo findUserByEmail(String email){
        SqlRowSet rs = template.queryForRowSet(Queries.SQL_GET_USER_BY_EMAIL, email);
        if (rs.next()){
            AccountInfo acc = new AccountInfo();
            acc.setEmail(rs.getString("email"));
            acc.setPasswordHash(rs.getString("passwordHash"));
            String userRole = getUserRole(rs.getString("userId"));
            if (null==userRole){
                return null;
            }
            acc.setRole(userRole);
            return acc;
        } else {
            return null;
        }
    }

    public String getUserRole(String userId){
        SqlRowSet rs = template.queryForRowSet(Queries.SQL_GET_USER_ROLE_BY_ID, userId);
        if (rs.next()){
            return rs.getString("role");
        }
        return null;
    }
    
}
