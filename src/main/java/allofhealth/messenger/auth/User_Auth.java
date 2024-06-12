package allofhealth.messenger.auth;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity(name = "user_auth")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "username"))
public class User_Auth implements UserDetails {

    @Id
    @Column(name = "username", columnDefinition = "VARCHAR(63)")
    private String username;

    @Column(name = "password")
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Builder.Default
    @Column(name = "account_non_expired")
    private boolean accountNonExpired = true;

    @Builder.Default
    @Column(name = "account_non_locked")
    private boolean accountNonLocked = true;

    @Builder.Default
    @Column(name = "credentials_non_expired")
    private boolean credentialsNonLocked = true;

    @Builder.Default
    @Column(name = "enabled")
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