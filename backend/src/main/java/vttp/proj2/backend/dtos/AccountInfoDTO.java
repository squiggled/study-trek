package vttp.proj2.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountInfoDTO {
    private String userId;
    private String email;
    private Long telegramId;

}
