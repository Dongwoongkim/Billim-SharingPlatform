package dblab.sharing_platform.dto.image;

import dblab.sharing_platform.domain.image.ProfileImage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileImageDto {

    private Long id;
    private String uniqueName;
    private String originName;

    public static ProfileImageDto toDto(ProfileImage profileImage) {
        if (profileImage != null) {
            return new ProfileImageDto(profileImage.getId(), profileImage.getUniqueName(), profileImage.getOriginName());
        }
        return null;
    }
}