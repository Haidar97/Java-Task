package org.eclipse.edc.extension.health;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Path("/health")
public class HealthApiController {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/edc_Sample";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "123456";

    @GET
    @Path("/status")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStatus() {
        return Response.ok(Map.of("status", "UP")).build();
    }

    @POST
    @Path("/users")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addUser(String userJson) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "INSERT INTO users (name, username, email, address, phone, website, company) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> userMap = mapper.readValue(userJson, Map.class);

                stmt.setString(1, (String) userMap.get("name"));
                stmt.setString(2, (String) userMap.get("username"));
                stmt.setString(3, (String) userMap.get("email"));
                stmt.setString(4, mapper.writeValueAsString(userMap.get("address")));
                stmt.setString(5, (String) userMap.get("phone"));
                stmt.setString(6, (String) userMap.get("website"));
                stmt.setString(7, mapper.writeValueAsString(userMap.get("company")));

                stmt.executeUpdate();
            }
            return Response.status(Response.Status.CREATED).build();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/users")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsers() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT * FROM users";
            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                List<Map<String, Object>> users = new ArrayList<>();
                ObjectMapper mapper = new ObjectMapper();

                while (rs.next()) {
                    Map<String, Object> user = Map.of(
                            "id", rs.getInt("id"),
                            "name", rs.getString("name"),
                            "username", rs.getString("username"),
                            "email", rs.getString("email"),
                            "address", mapper.readValue(rs.getString("address"), Map.class),
                            "phone", rs.getString("phone"),
                            "website", rs.getString("website"),
                            "company", mapper.readValue(rs.getString("company"), Map.class)
                    );
                    users.add(user);
                }
                return Response.ok(users).build();
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }
}
