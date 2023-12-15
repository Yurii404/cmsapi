package com.sombra.cmsapi.authservice.entity;

import com.sombra.cmsapi.authservice.enumerated.TokenType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tokens", catalog = "jwt_security")
public class Token {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private String id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String accessToken;
    private String refreshToken;

    @Enumerated(EnumType.STRING)
    private TokenType tokenType;

    private boolean expired;

    private boolean revoked;

    @Override
    public String toString() {
        return "Token{" +
                "id=" + id +
                ", user=" + user +
                ", accessToken='" + accessToken + '\'' +
                ", refreshToken='" + refreshToken + '\'' +
                ", tokenType=" + tokenType +
                ", expired=" + expired +
                ", revoked=" + revoked +
                '}';
    }
}
