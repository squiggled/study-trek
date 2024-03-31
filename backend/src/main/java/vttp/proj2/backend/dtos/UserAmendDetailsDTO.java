package vttp.proj2.backend.dtos;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAmendDetailsDTO {
    private String firstName;
    private String lastName;
    private List<String> interests;
}
