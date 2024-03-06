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
        return false;
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
    
}
