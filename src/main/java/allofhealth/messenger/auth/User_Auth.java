package allofhealth.messenger.auth;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@org.springframework.data.relational.core.mapping.Table(name = "user_auth")
public class User_Auth implements UserDetails {

    @Id
    @Column
    private String username;

    @Column
    private String password;

    @Column
    private Role role;

    @Builder.Default
    @Column
    private boolean accountNonExpired = true;

    @Builder.Default
    @Column
    private boolean accountNonLocked = true;

    @Builder.Default
    @Column
    private boolean credentialsNonLocked = true;

    @Builder.Default
    @Column
    private boolean enabled = true;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRole().getAuthorities();
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.credentialsNonLocked;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
}