package com.awashbank.supply_chain.jwt;

import com.awashbank.supply_chain.user.model.JwtRequestModel;
import com.awashbank.supply_chain.user.model.UserDetailModel;
import com.awashbank.supply_chain.user.model.UserDetailRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import java.time.LocalDateTime;
import java.util.Hashtable;


@RestController
@CrossOrigin
public class JwtController {
    private final TokenManager tokenManager;
    private final AuthenticationManager authenticationManager;
    private final UserDetailRepository userDetailRepository;
    private final UserDetailRepository usdRepository;

    @Value("${serverStatus}")
    private String server;

    @Value("${testAD}")
    private String testAD;

    @Value("${prodAD}")
    private String prodAD;

    @Value("${testEXT}")
    private String testEXT;

    @Value("${prodEXT}")
    private String prodEXT;

    public JwtController(AuthenticationManager authenticationManager, TokenManager tokenManager, UserDetailRepository userDetailRepository, UserDetailRepository usdRepository) {
        this.authenticationManager = authenticationManager;
        this.tokenManager = tokenManager;
        this.userDetailRepository = userDetailRepository;
        this.usdRepository = usdRepository;
    }


    @PostMapping("/scf/api/v1/auth/getToken")
    public ResponseEntity<JsonNode> createToken(@RequestBody JwtRequestModel
                                                        request) throws Exception {

        String ip = null;
        String ext = null;
        String resp = null;

        try {
            if (server.equals("test")) {
                ip = testAD;
                ext = testEXT;
            } else {
                ip = prodAD;
                ext = prodEXT;
            }

        } catch (Exception e) {
            resp = e.getMessage();
            e.printStackTrace();
        }

        boolean status = checkLdap(ip.trim(), request.getUsername().trim() + ext.trim(), request.getPassword().trim());

        JsonNode res;
        ObjectMapper ob = new ObjectMapper();

        if (status) {

            UserDetailModel userDetailModel = usdRepository.byUser(request.getUsername());

            if (userDetailModel == null) {

                UserDetailModel udm = new UserDetailModel();
                udm.setCreate_date(String.valueOf(LocalDateTime.now()));
                udm.setUsername(request.getUsername());
                udm.setDesignation("user");
                udm.setStatus(1);

                usdRepository.save(udm);

                final String token = tokenManager.generateToken(request.getUsername());
                UserDetailModel usd = userDetailRepository.byUser(request.getUsername());

                if (usd.getStatus() == 1) {

                        res = ob.readTree("{" +
                                "\"status\":true," +
                                "\"token\":\"" + token + "\"}");

                        return new ResponseEntity<>(res, HttpStatus.OK);
                } else {
                    res = ob.readTree("{" +
                            "\"status\":false," +
                            "\"response\":\"This user is banned from login please contact IT!!!\"}");

                    return new ResponseEntity<>(res, HttpStatus.UNAUTHORIZED);
                }

            } else {

                final String token = tokenManager.generateToken(request.getUsername());
                 UserDetailModel usd = userDetailRepository.byUser(request.getUsername());

                if (usd.getStatus() == 1) {

                        res = ob.readTree("{" +
                                "\"status\":true," +
                                "\"token\":\"" + token + "\"}");

                        return new ResponseEntity<>(res, HttpStatus.OK);

                } else {
                    res = ob.readTree("{" +
                            "\"status\":false," +
                            "\"response\":\"This user is banned from login please contact IT!!!\"}");

                    return new ResponseEntity<>(res, HttpStatus.UNAUTHORIZED);
                }
            }

        } else {
            res = ob.readTree("{" +
                    "\"status\":false," +
                    "\"response\":\"Wrong Credential\"}");

            return new ResponseEntity<>(res, HttpStatus.UNAUTHORIZED);
        }
    }

    private String authenticate(String username, String password) {

        String resp = "";
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException | BadCredentialsException e) {
            resp = e.getMessage();
        }

        return resp;
    }

    public boolean checkLdap(String url,String username,String password){
        LdapContext ctx = null;
        boolean status = false;
        try {
            Hashtable<String, String> env = new Hashtable<String, String>();
            env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            env.put(Context.SECURITY_AUTHENTICATION, "Simple");
            env.put(Context.SECURITY_PRINCIPAL, username);//input user & password for access to ldap
            env.put(Context.SECURITY_CREDENTIALS, password);
            env.put(Context.PROVIDER_URL, "ldap://" + url);
            env.put(Context.REFERRAL, "follow");
            ctx = new InitialLdapContext(env, null);

            System.out.println("LDAP Connection: COMPLETE");
            status = true;

        } catch (NamingException nex) {
            System.out.println("LDAP Connection: FAILED");
            //nex.printStackTrace();
        }

        return status;
    }

}
