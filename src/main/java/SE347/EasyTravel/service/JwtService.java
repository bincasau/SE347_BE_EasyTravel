package SE347.EasyTravel.service;

import SE347.EasyTravel.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    // sinh token tuwf user
    public String generateToken(User user){
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", user.getRole());
        return generateToken(extraClaims, user.getUsername());
    }
    // sinh token co claims tuy chon
    public String generateToken(Map<String, Object> extraClaims, String username){
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSignInkey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // giai ma secretKey
    private Key getSignInkey(){
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    // lay username
    public String extractUsername(String token){
        return extractClaim(token, Claims::getSubject);
    }
    // lay 1 claims bat ky
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    // giai ma toan bo claim tu token
    private Claims extractAllClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(getSignInkey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    // kiem tra token co hop le khong
    public boolean isTokenValid(String token, UserDetails userDetails){
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }
    // kiem tra het han chua
    private boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }
    // lay thoi gian het han
    private Date extractExpiration(String token){
        return extractClaim(token, Claims::getExpiration);
    }

    public void addJwtCookie(HttpServletResponse response, String token) {
        jakarta.servlet.http.Cookie cookie = new jakarta.servlet.http.Cookie("jwt", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(24 * 60 * 60);
        response.addCookie(cookie);
    }

}
