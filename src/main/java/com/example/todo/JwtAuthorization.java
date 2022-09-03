package com.example.todo;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

public class JwtAuthorization extends OncePerRequestFilter{
    private final String HEADER="Authorization";
    private final String PREFIX="Bearer";
    private final String SECRET="TODO";
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // TODO Auto-generated method stub
        if(existJWT(request)){
            Claims claims =validateToken(request);
            setUpSpringAuthentication(claims);
        }else{
            SecurityContextHolder.clearContext();
        }
        filterChain.doFilter(request, response);
    }
    private void setUpSpringAuthentication(Claims claims){
        @SuppressWarnings("unchecked")
        List<String>authorities=(List)claims.get("authorities");
        UsernamePasswordAuthenticationToken auth=new UsernamePasswordAuthenticationToken(claims.getSubject(),null,
        authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
    private Claims validateToken(HttpServletRequest request){
        String jwtToken=request.getHeader(HEADER).replace(PREFIX, "");
        return Jwts.parser().setSigningKey(SECRET.getBytes()).parseClaimsJws(jwtToken).getBody();
    }
    private Boolean existJWT(HttpServletRequest request){
        String header = request.getHeader(HEADER);
        if(header==null || !header.startsWith(PREFIX)){
            return false;
        }
        return true;
    }
    

}
