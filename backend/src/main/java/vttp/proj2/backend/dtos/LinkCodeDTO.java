package vttp.proj2.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LinkCodeDTO {
   
    private String linkCode;
    private String userId;

}
