package dao;
import java.nio.file.*;
import javax.servlet.MultipartConfigElement;
import javax.servlet.http.Part;
import static spark.Spark.*;

import com.azure.core.http.HttpClient;
import com.azure.core.http.HttpRequest;
import com.azure.core.http.HttpResponse;
import com.azure.storage.blob.*;
import com.azure.storage.blob.models.*;
import com.azure.storage.blob.sas.BlobSasPermission;
import com.azure.storage.blob.sas.BlobServiceSasSignatureValues;
import java.util.Arrays;
import java.util.List;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.ai.openai.models.ChatChoice;
import com.azure.ai.openai.models.ChatCompletions;
import com.azure.ai.openai.models.ChatCompletionsOptions;
import com.azure.ai.openai.models.ChatRequestAssistantMessage;
import com.azure.ai.openai.models.ChatRequestMessage;
import com.azure.ai.openai.models.ChatRequestSystemMessage;
import com.azure.ai.openai.models.ChatRequestUserMessage;
import com.azure.ai.openai.models.ChatResponseMessage;
import com.azure.ai.openai.models.CompletionsUsage;
import com.azure.core.credential.AzureKeyCredential;
import com.azure.core.util.Configuration;
import org.json.JSONObject;
import javax.servlet.http.Part;
import java.nio.file.Paths;
import java.util.UUID;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.OffsetDateTime;

import org.json.JSONArray;
import org.json.JSONObject;

public class App {
	private static final String url = "jdbc:postgresql://pucmg5.postgres.database.azure.com:5432/postgres";
	private static final String user = "adm"; // ou apenas "artur70152@ti270.postgres.database.azure.com" 
	//private static final String password = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsIng1dCI6IkNOdjBPSTNSd3FsSEZFVm5hb01Bc2hDSDJYRSIsImtpZCI6IkNOdjBPSTNSd3FsSEZFVm5hb01Bc2hDSDJYRSJ9.eyJhdWQiOiJodHRwczovL29zc3JkYm1zLWFhZC5kYXRhYmFzZS53aW5kb3dzLm5ldCIsImlzcyI6Imh0dHBzOi8vc3RzLndpbmRvd3MubmV0Lzc4OTk2OTgzLTkwZTYtNGUxYS05MTU2LTNlYTQ3ZmYyNTQyNC8iLCJpYXQiOjE3NDg2MzgwNDQsIm5iZiI6MTc0ODYzODA0NCwiZXhwIjoxNzQ4NjQyNTU0LCJhY3IiOiIxIiwiYWlvIjoiQVpRQWEvOFpBQUFBZWhpNWVHVk5WM2NacmNmdWhZdWthU3Q3ZUFxWDVaenhmU25CNE1JSkoyeGpPcFIzZHVVRUo4SEhJVkVQRzZ0VmF3QTc2UmVZaTlUNmZNUUVWWm04WFpWeHpqOHRLMjZXQ2sxVkJ6QVp6SnBMaS9OOTd6dUdkTVZ0RUVZczBleUJtV3Arb0xhd1VzMTc3NUlVQkZ0OEtDRk1MZ3M2MnZNZ2tuVWFpT2N1SktrcG82OTlpWGd0azFZUzkxSENYL04zIiwiYWx0c2VjaWQiOiIxOmxpdmUuY29tOjAwMDM0MDAxMjQyNDIyRDMiLCJhbXIiOlsicHdkIiwibWZhIl0sImFwcGlkIjoiMDRiMDc3OTUtOGRkYi00NjFhLWJiZWUtMDJmOWUxYmY3YjQ2IiwiYXBwaWRhY3IiOiIwIiwiZW1haWwiOiJhcnR1cjcwMTUyQGdtYWlsLmNvbSIsImZhbWlseV9uYW1lIjoiZmVybmFuZGVzIiwiZ2l2ZW5fbmFtZSI6ImFydHVyIiwiZ3JvdXBzIjpbIjRmOGY5ZGQ2LTcyYzYtNDdjYy1iYmEyLTZhYmJkZWJlMzc3NyJdLCJpZHAiOiJsaXZlLmNvbSIsImlkdHlwIjoidXNlciIsImlwYWRkciI6IjI4MDQ6MTRjOjViYjE6OGIxMToyMjI0OjU0ZTM6YWIwNToxNTk3IiwibmFtZSI6ImFydHVyIGZlcm5hbmRlcyIsIm9pZCI6ImQ2NTZiOTRhLTQ1N2ItNDI2ZC1hYjVmLTUzMWMzNTE1OWQyMyIsInB1aWQiOiIxMDAzMjAwNDlBNkFGNDkwIiwicmgiOiIxLkFXTUJnMm1aZU9hUUdrNlJWajZrZl9KVUpGRFlQQkxmMmIxQWxOWEo4SHRfb2dQSUFhbGpBUS4iLCJzY3AiOiJ1c2VyX2ltcGVyc29uYXRpb24iLCJzaWQiOiIwMDUwZTcwOS0wODUyLThmOGMtNWI2Zi00NTM0YzM2NzdkZGIiLCJzdWIiOiJjQmdrSE5UTzJKTUs2ZXdVTFRDel9XU3VYV1I0Rm5VWjBtVEV5ZEUyVUJJIiwidGlkIjoiNzg5OTY5ODMtOTBlNi00ZTFhLTkxNTYtM2VhNDdmZjI1NDI0IiwidW5pcXVlX25hbWUiOiJsaXZlLmNvbSNhcnR1cjcwMTUyQGdtYWlsLmNvbSIsInV0aSI6Ikw5b2FfSTEydDBXOWhZNUp1cjRlQVEiLCJ2ZXIiOiIxLjAiLCJ4bXNfZnRkIjoiQlFRTkNPTlhTYWFvVVBvMWtzQTRUUDVxcHFkZnVndkNLeUNMdlN0X2FuUUJkWE51YjNKMGFDMWtjMjF6IiwieG1zX2lkcmVsIjoiMSAyNCJ9.VkcgQHboj6asRnAyJRRXHocp39vzd1tFhYIAoxJTVTL7S2-pit5MplccCyH1o-7TVIh-ojcX1jEcNcqZqH_imj1idoVOz-Hnt59beAgyVz0Txrn--zF8s6GcLb9aH2ZKjTF_HL0ShPAXWdQqPuvKg7ufBS4u-bhu18El42LcZyoxFbHOD_qUYfO-bcpYm1_DvpXJ22Zcqo-2pwGQrOgglRFh8XK2zxrXZANzzB1F5gEYt3ER0cw4Di-Gp0NlfeGTcJWBB2G8L88U_hJ0nq4eQHOcCCuPpqDw6uvpmqdXD-P8LbXasVg8HcZxbPO1HnS3wsUA-FtHSGuX0uPagQ0mkw"; 
	private static final String password = "@Pucminas70152";
	
	
	public static void main(String[] args) {

		String portStr = System.getenv("PORT");
		int porta = (portStr != null) ? Integer.parseInt(portStr) : 3000;
		port(porta);
		staticFiles.location("public");
       // staticFiles.externalLocation("src/main/resources/static");

        // Exemplo de rota de teste
        get("/hello", (req, res) -> "BORAAAAA!");

        // Rota equivalente ao getUserByEmail
        
        get("/get-user/:email", (req, res) -> {
            String email = req.params(":email");

            try {
                JSONObject user = getUserByEmail(email);

                if (user == null) {
                    res.status(404);
                    return new JSONObject().put("error", "Usuário não encontrado").toString();
                }

                res.type("application/json");
                return user.toString();

            } catch (Exception e) {
                e.printStackTrace();
                res.status(500);
                return new JSONObject().put("error", "Erro ao buscar usuário").toString();
            }
        });
        get("/users", (req, res) -> {
            JSONArray users = new JSONArray();

            try (Connection conn = DriverManager.getConnection(url, user, password)) {
                String sql = "SELECT name, normal, doacoes, email, senha, foto FROM usuario";
                try (PreparedStatement stmt = conn.prepareStatement(sql);
                     ResultSet rs = stmt.executeQuery()) {

                    while (rs.next()) {
                        JSONObject user = new JSONObject();
                        user.put("name", rs.getString("name"));
                        user.put("normal", rs.getBoolean("normal"));
                        user.put("doacoes", rs.getDouble("doacoes"));
                        user.put("email", rs.getString("email"));
                        user.put("senha", rs.getString("senha"));
                        user.put("foto", rs.getString("foto"));
                        users.put(user);
                    }
                }

                res.type("application/json");
                return users.toString();
            } catch (Exception e) {
                e.printStackTrace();
                res.status(500);
                return new JSONObject().put("message", "Erro ao buscar usuários").toString();
            }
        });

        post("/save-user", (req, res) -> {
            JSONObject body = new JSONObject(req.body());
            String name = body.getString("name");
            boolean normal = body.getBoolean("normal");
            double doacoes = body.getDouble("doacoes");
            String email = body.getString("email");
            String senha = body.getString("senha");
            String foto = body.optString("foto", "");

            try (Connection conn = DriverManager.getConnection(url, user, password)) {
                // Verifica se já existe usuário com o mesmo e-mail
                String checkSql = "SELECT COUNT(*) FROM usuario WHERE email = ?";
                try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                    checkStmt.setString(1, email);
                    ResultSet rs = checkStmt.executeQuery();
                    if (rs.next() && rs.getInt(1) > 0) {
                        res.status(400);
                        return new JSONObject().put("message", "Erro: E-mail já está em uso no banco de dados.").toString();
                    }
                }

                // Inserir novo usuário
                String insertSql = "INSERT INTO usuario (name, normal, doacoes, email, senha, foto, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?,now(),now())";
                try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
                    stmt.setString(1, name);
                    stmt.setBoolean(2, normal);
                    stmt.setDouble(3, doacoes);
                    stmt.setString(4, email);
                    stmt.setString(5, senha);
                    stmt.setString(6, foto);
                    stmt.executeUpdate();
                }

                res.status(201);
                return new JSONObject().put("message", "Usuário salvo com sucesso!").toString();
            } catch (Exception e) {
                e.printStackTrace();
                res.status(500);
                return new JSONObject().put("message", "Erro ao salvar usuário.").toString();
            }
        });
  
        post("/upload", (req, res) -> {
            req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/tmp"));

            Part filePart = req.raw().getPart("file");
            String fileNameOriginal = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
            String fileName = UUID.randomUUID() + "-" + fileNameOriginal;

            InputStream inputStream = filePart.getInputStream();

            String connectStr = "DefaultEndpointsProtocol=https;AccountName=arturstorage123;AccountKey=rag3k195dvduzs6U18xzxJs9/WGaUgRG6OIrueZrRTDHGwbIT5qThCiJ50SxgqaULsdy+A+LJI2/+AStMcaDWQ==;EndpointSuffix=core.windows.net";
            String containerName = "upload";

            BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                    .connectionString(connectStr)
                    .buildClient();

            BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
            BlobClient blobClient = containerClient.getBlobClient(fileName);

            // Upload
            blobClient.upload(inputStream, filePart.getSize(), true);

            // Gerar token SAS válido por 24h
            BlobServiceSasSignatureValues sasValues = new BlobServiceSasSignatureValues(
                OffsetDateTime.now().plusHours(24),
                new BlobSasPermission().setReadPermission(true)
            );

            String sasToken = blobClient.generateSas(sasValues);
            String blobUrlWithSas = blobClient.getBlobUrl() + "?" + sasToken;

            JSONObject result = new JSONObject();
            result.put("filePath", blobUrlWithSas); // URL com token SAS
            res.type("application/json");
            return result.toString();
        });

        post("/add-message", (req, res) -> {
            JSONObject body = new JSONObject(req.body());
            String usuario = body.getString("usuario");
            String texto = body.getString("texto");
            String remetente = body.getString("remetente");

            try (Connection conn = DriverManager.getConnection(url, user, password)) {
                // Buscar destinatário
                String userSql = "SELECT id, name, foto FROM usuario WHERE email = ?";
                PreparedStatement userStmt = conn.prepareStatement(userSql);
                userStmt.setString(1, usuario);
                ResultSet rsDest = userStmt.executeQuery();

                if (!rsDest.next()) {
                    res.status(404);
                    return new JSONObject().put("error", "Destinatário não encontrado").toString();
                }
                int destinatarioId = rsDest.getInt("id");

                // Buscar remetente
                userStmt.setString(1, remetente);
                ResultSet rsRem = userStmt.executeQuery();

                if (!rsRem.next()) {
                    res.status(404);
                    return new JSONObject().put("error", "Remetente não encontrado").toString();
                }
                String nomeRemetente = rsRem.getString("name");
                String fotoRemetente = rsRem.getString("foto");

                // Verifica se já existe uma conversa
                String selectConversa = "SELECT id, mensagens FROM mensagem WHERE usuario = ? AND nome = ?";
                PreparedStatement selectStmt = conn.prepareStatement(selectConversa);
                selectStmt.setString(1, usuario);
                selectStmt.setString(2, nomeRemetente);
                ResultSet rsConversa = selectStmt.executeQuery();

                JSONArray mensagens = new JSONArray();

                if (rsConversa.next()) {
                    String mensagensJson = rsConversa.getString("mensagens");
                    if (mensagensJson != null && !mensagensJson.isEmpty()) {
                        mensagens = new JSONArray(mensagensJson);
                    }

                    // Adiciona nova mensagem
                    JSONObject novaMensagem = new JSONObject();
                    novaMensagem.put("remetente", remetente);
                    novaMensagem.put("texto", texto);
                    novaMensagem.put("timestamp", System.currentTimeMillis());

                    mensagens.put(novaMensagem);

                    // Atualiza a conversa
                    String updateSql = "UPDATE mensagem SET mensagens = ? WHERE id = ?";
                    PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                    updateStmt.setObject(1, mensagens.toString(), java.sql.Types.OTHER);

                    updateStmt.setInt(2, rsConversa.getInt("id"));
                    updateStmt.executeUpdate();

                } else {
                    // Não existe, cria nova
                    String insertSql = "INSERT INTO mensagem (usuario, usuario_id, mensagens, nome, foto,created_at, updated_at) VALUES (?, ?, ?, ?, ?, now(), now())";
                    PreparedStatement insertStmt = conn.prepareStatement(insertSql);
                    JSONObject novaMensagem = new JSONObject();
                    novaMensagem.put("remetente", remetente);
                    novaMensagem.put("texto", texto);
                    novaMensagem.put("timestamp", System.currentTimeMillis());

                    mensagens.put(novaMensagem);

                    insertStmt.setString(1, usuario);
                    insertStmt.setInt(2, destinatarioId);
                    insertStmt.setObject(3, mensagens.toString(), java.sql.Types.OTHER); // ✅ CERTO

                    insertStmt.setString(4, nomeRemetente);
                    insertStmt.setString(5, fotoRemetente);

                    insertStmt.executeUpdate();
                }

                res.status(200);
                return new JSONObject().put("success", true).put("mensagem", mensagens.getJSONObject(mensagens.length() - 1)).toString();

            } catch (Exception e) {
                e.printStackTrace();
                res.status(500);
                return new JSONObject().put("error", "Erro ao enviar mensagem").toString();
            }
        });
        
        post("/update-descricao", (req, res) -> {
            JSONObject body = new JSONObject(req.body());
            int arteId = body.getInt("arteId");
            String novaDescricao = body.getString("descricao");

            try (Connection conn = DriverManager.getConnection(url, user, password)) {
                String sql = "UPDATE arte SET descricao = ?, updated_at = now() WHERE id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, novaDescricao);
                    stmt.setInt(2, arteId);
                    stmt.executeUpdate();
                }

                res.status(200);
                return new JSONObject().put("message", "Descrição atualizada com sucesso!").toString();
            } catch (Exception e) {
                e.printStackTrace();
                res.status(500);
                return new JSONObject().put("error", "Erro ao atualizar descrição.").toString();
            }
        });

        get("/get-conversation/:usuario/:destinatario", (req, res) -> {
            String usuario = req.params(":usuario");
            String destinatario = req.params(":destinatario");

            try (Connection conn = DriverManager.getConnection(url, user, password)) {
                JSONArray mensagens = new JSONArray();

                String sql = "SELECT mensagens FROM mensagem WHERE (usuario = ? AND nome = ?) OR (usuario = ? AND nome = ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, usuario);
                stmt.setString(2, destinatario);
                stmt.setString(3, destinatario);
                stmt.setString(4, usuario);

                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    String mensagensJson = rs.getString("mensagens");
                    if (mensagensJson != null && !mensagensJson.isEmpty()) {
                        JSONArray arr = new JSONArray(mensagensJson);
                        for (int i = 0; i < arr.length(); i++) {
                            mensagens.put(arr.getJSONObject(i));
                        }
                    }
                }

                // Ordena por timestamp
                mensagens = new JSONArray(((JSONArray) mensagens.toList().stream()
                    .sorted((o1, o2) -> {
                        JSONObject m1 = new JSONObject((java.util.Map<?, ?>) o1);
                        JSONObject m2 = new JSONObject((java.util.Map<?, ?>) o2);
                        return Long.compare(m1.getLong("timestamp"), m2.getLong("timestamp"));
                    })).toList());

                res.type("application/json");
                return mensagens.toString();

            } catch (Exception e) {
                e.printStackTrace();
                res.status(500);
                return new JSONObject().put("error", "Erro ao buscar conversa").toString();
            }
        });

     // Rota para adicionar usuário
        post("/add-user", (req, res) -> {
            JSONObject body = new JSONObject(req.body());
            String name = body.getString("name");
            boolean normal = body.getBoolean("normal");
            double doacoes = body.getDouble("doacoes");
            String email = body.getString("email");
            String senha = body.getString("senha");
            String foto = body.getString("foto");

            try {
                JSONObject result = addUser(name, normal, doacoes, email, senha, foto);
                res.status(201);
                return result.toString();
            } catch (Exception e) {
                e.printStackTrace();
                res.status(500);
                return new JSONObject().put("error", "Erro ao salvar usuário").toString();
            }
        });
     // Rota para atualizar foto de perfil
        post("/update-profile-photo", (req, res) -> {
            JSONObject body = new JSONObject(req.body());
            String email = body.getString("email");
            String foto = body.getString("foto");

            try {
                JSONObject result = updateProfilePhoto(email, foto);
                res.status(200);
                return result.toString();
            } catch (Exception e) {
                e.printStackTrace();
                res.status(500);
                return new JSONObject().put("error", "Erro ao atualizar foto de perfil").toString();
            }
        });
     // Rota para buscar mensagens de um usuário
        get("/get-messages/:email", (req, res) -> {
            String email = req.params(":email");

            try {
                JSONArray mensagens = getMessages(email);
                res.type("application/json");
                return mensagens.toString();
            } catch (Exception e) {
                e.printStackTrace();
                res.status(500);
                return new JSONObject().put("error", "Erro ao buscar mensagens").toString();
            }
        });
     // Rota para listar todos os usuários
        get("/get-all-users", (req, res) -> {
            try {
                JSONArray users = getAllUsers();
                res.type("application/json");
                return users.toString();
            } catch (Exception e) {
                e.printStackTrace();
                res.status(500);
                return new JSONObject().put("error", "Erro ao buscar usuários").toString();
            }
        });

        get("/get-all-artes", (req, res) -> {
            try {
                JSONArray artes = getAllArtes();
                res.type("application/json");
                return artes.toString();
            } catch (Exception e) {
                e.printStackTrace();
                res.status(500);
                return new JSONObject().put("error", "Erro ao buscar todas as artes").toString();
            }
        });
        
        
        
        get("/popular-artes", (req, res) -> {
            try (Connection conn = DriverManager.getConnection(url, user, password)) {
                String userSql = "SELECT id, name FROM usuario";
                try (PreparedStatement userStmt = conn.prepareStatement(userSql);
                     ResultSet users = userStmt.executeQuery()) {

                    String[] imagens = {"/images/arte1.jpg", "/images/arte2.jpg", "/images/arte3.jpg"};

                    String insertSql = "INSERT INTO arte (usuario_id, arte, name, curtidas) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                        while (users.next()) {
                            int usuarioId = users.getInt("id");
                            String usuarioName = users.getString("name");
                            for (int i = 0; i < 3; i++) {
                                insertStmt.setInt(1, usuarioId);
                                insertStmt.setString(2, imagens[i % imagens.length]);
                                insertStmt.setString(3, "Arte " + (i + 1) + " de " + usuarioName);
                                insertStmt.setInt(4, 0);
                                insertStmt.addBatch();
                            }
                        }
                        insertStmt.executeBatch();
                    }
                }
                res.type("application/json");
                return new JSONObject().put("message", "Banco populado com 3 artes para cada usuário.").toString();
            } catch (Exception e) {
                e.printStackTrace();
                res.status(500);
                return new JSONObject().put("error", "Erro ao popular banco com artes.").toString();
            }
        });
        
        
        post("/add-arte", (req, res) -> {
            JSONObject body = new JSONObject(req.body());
            String email = body.getString("email");
            String arte = body.getString("arte");
            String name = body.optString("name");
            int curtidas = body.optInt("curtidas", 0);

            try (Connection conn = DriverManager.getConnection(url, user, password)) {
                String userSql = "SELECT id FROM usuario WHERE email = ?";
                try (PreparedStatement userStmt = conn.prepareStatement(userSql)) {
                    userStmt.setString(1, email);
                    try (ResultSet rs = userStmt.executeQuery()) {
                        if (!rs.next()) {
                            res.status(404);
                            return new JSONObject().put("message", "Usuário não encontrado.").toString();
                        }
                        int usuarioId = rs.getInt("id");

                        String insertSql = "INSERT INTO arte (usuario_id, arte, name, curtidas, created_at,updated_at) VALUES (?, ?, ?, ?,now(),now())";
                        try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                            insertStmt.setInt(1, usuarioId);
                            insertStmt.setString(2, arte);
                            insertStmt.setString(3, name);
                            insertStmt.setInt(4, curtidas);
                            insertStmt.executeUpdate();
                        }
                    }
                }
                res.status(201);
                return new JSONObject().put("message", "Arte adicionada com sucesso.").toString();
            } catch (Exception e) {
                e.printStackTrace();
                res.status(500);
                return new JSONObject().put("error", "Erro ao adicionar arte.").toString();
            }
        });

        post("/descrever-arte", (req, res) -> {
            JSONObject body = new JSONObject(req.body());
            String urlImagem = body.getString("url");
          String descricao1=  descreverImagemComAzure(urlImagem);
            String descricao = enviarPromptParaAzureOpenAI(descricao1);
            JSONObject resp = new JSONObject();
            resp.put("descricao", descricao);
            return resp.toString();
        });
        
        post("/update-curtidas", (req, res) -> {
            JSONObject body = new JSONObject(req.body());
            System.out.println(body);
            String email = body.getString("email");
            int arteId = body.getInt("arteIndex");
            String usuario = body.getString("usuario");

            try (Connection conn = DriverManager.getConnection(url, user, password)) {
                // Checar usuário
                String userSql = "SELECT id FROM usuario WHERE email = ?";
                try (PreparedStatement userStmt = conn.prepareStatement(userSql)) {
                    userStmt.setString(1, email);
                    try (ResultSet rs = userStmt.executeQuery()) {
                        if (!rs.next()) {
                            res.status(404);
                            return new JSONObject().put("error", "Usuário não encontrado").toString();
                        }
                    }
                }

                // Checar curtida
                String checkSql = "SELECT * FROM curtidaarte WHERE arte_id = ? AND usuario = ?";
                try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                    checkStmt.setInt(1, arteId);
                    checkStmt.setString(2, usuario);
                    try (ResultSet rs = checkStmt.executeQuery()) {
                        if (rs.next()) {
                            // Descurtir
                            String delSql = "DELETE FROM curtidaarte WHERE arte_id = ? AND usuario = ?";
                            try (PreparedStatement delStmt = conn.prepareStatement(delSql)) {
                                delStmt.setInt(1, arteId);
                                delStmt.setString(2, usuario);
                                delStmt.executeUpdate();
                            }

                            String updSql = "UPDATE arte SET curtidas = GREATEST(curtidas - 1, 0) WHERE id = ?";
                            try (PreparedStatement updStmt = conn.prepareStatement(updSql)) {
                                updStmt.setInt(1, arteId);
                                updStmt.executeUpdate();
                            }

                            return new JSONObject().put("message", "Descurtido").toString();
                        } else {
                            // Curtir
                        	String insSql = "INSERT INTO curtidaarte (arte_id, usuario, created_at, updated_at) VALUES (?, ?, now(), now())";
                        	try (PreparedStatement insStmt = conn.prepareStatement(insSql)) {
                        	    insStmt.setInt(1, arteId);
                        	    insStmt.setString(2, usuario);
                        	    insStmt.executeUpdate();
                        	}
                            String updSql = "UPDATE arte SET curtidas = curtidas + 1 WHERE id = ?";
                            try (PreparedStatement updStmt = conn.prepareStatement(updSql)) {
                                updStmt.setInt(1, arteId);
                                updStmt.executeUpdate();
                            }

                            return new JSONObject().put("message", "Curtido").toString();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                res.status(500);
                return new JSONObject().put("error", "Erro ao atualizar curtidas").toString();
            }
        });

        
        
        post("/remove-arte", (req, res) -> {
            JSONObject body = new JSONObject(req.body());
            int arteId = body.getInt("arteId");

            try (Connection conn = DriverManager.getConnection(url, user, password)) {
                String delSql = "DELETE FROM arte WHERE id = ?";
                try (PreparedStatement delStmt = conn.prepareStatement(delSql)) {
                    delStmt.setInt(1, arteId);
                    int affected = delStmt.executeUpdate();
                    if (affected == 0) {
                        res.status(404);
                        return new JSONObject().put("message", "Arte não encontrada.").toString();
                    }
                }
                return new JSONObject().put("message", "Arte removida com sucesso.").toString();
            } catch (Exception e) {
                e.printStackTrace();
                res.status(500);
                return new JSONObject().put("error", "Erro ao remover arte.").toString();
            }
        });
        post("/add-comment", (req, res) -> {
            JSONObject body = new JSONObject(req.body());
            int arteId = body.getInt("arteId");
            String usuario = body.getString("usuario");
            String texto = body.getString("texto");
        
            try (Connection conn = DriverManager.getConnection(url, user, password)) {
                String insSql = "INSERT INTO comentario_arte (arte_id, usuario, texto,created_at, updated_at) VALUES (?, ?, ?,now(),now())";
                try (PreparedStatement insStmt = conn.prepareStatement(insSql)) {
                    insStmt.setInt(1, arteId);
                    insStmt.setString(2, usuario);
                    insStmt.setString(3, texto);
                    insStmt.executeUpdate();
                }
                res.status(201);
                return new JSONObject().put("message", "Comentário adicionado com sucesso.").toString();
            } catch (Exception e) {
                e.printStackTrace();
                res.status(500);
                return new JSONObject().put("error", "Erro ao adicionar comentário").toString();
            }
        });
        get("/get-artes/:email", (req, res) -> {
            String email = req.params(":email");

            try (Connection conn = DriverManager.getConnection(url, user, password)) {
                String userSql = "SELECT id FROM usuario WHERE email = ?";
                try (PreparedStatement userStmt = conn.prepareStatement(userSql)) {
                    userStmt.setString(1, email);
                    try (ResultSet rs = userStmt.executeQuery()) {
                        if (!rs.next()) {
                            res.status(404);
                            return new JSONObject().put("error", "Usuário não encontrado").toString();
                        }
                        int usuarioId = rs.getInt("id");

                        JSONArray artes = new JSONArray();
                        String sql = "SELECT id, arte FROM arte WHERE usuario_id = ?";
                        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                            stmt.setInt(1, usuarioId);
                            try (ResultSet rsArtes = stmt.executeQuery()) {
                                while (rsArtes.next()) {
                                    JSONObject arte = new JSONObject();
                                    arte.put("id", rsArtes.getInt("id"));
                                    arte.put("arte", rsArtes.getString("arte"));
                                    artes.put(arte);
                                }
                            }
                        }
                        res.type("application/json");
                        return artes.toString();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                res.status(500);
                return new JSONObject().put("error", "Erro ao buscar artes").toString();
            }
        });

    }
   
	public static String descreverImagemComAzure(String imageUrl) throws Exception {
	    String endpoint = "https://aiartur1.cognitiveservices.azure.com/";
	    String key = "BrJWNTSyJFp3PtucCcFzzb6DsbXVBI1HZmrxgcBUfrEmHNLYtEB1JQQJ99BFACYeBjFXJ3w3AAAFACOGcrsT";
	    String uriBase = endpoint + "vision/v3.2/describe?maxCandidates=5&language=pt";


	    URL url = new URL(uriBase);
	    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	    connection.setRequestMethod("POST");
	    connection.setRequestProperty("Ocp-Apim-Subscription-Key", key);
	    connection.setRequestProperty("Content-Type", "application/json");
	    connection.setDoOutput(true);

	    String jsonBody = "{\"url\":\"" + imageUrl + "\"}";
	    try (OutputStream os = connection.getOutputStream()) {
	        os.write(jsonBody.getBytes("UTF-8"));
	    }

	    StringBuilder response;
	    try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
	        String inputLine;
	        response = new StringBuilder();
	        while ((inputLine = in.readLine()) != null) {
	            response.append(inputLine);
	        }
	    }

	    JSONObject json = new JSONObject(response.toString());
	    JSONArray captions = json.getJSONObject("description").getJSONArray("captions");
	    if (captions.length() > 0) {
	        StringBuilder todas = new StringBuilder();
	        for (int i = 0; i < captions.length(); i++) {
	            String texto = captions.getJSONObject(i).getString("text");
	            double confianca = captions.getJSONObject(i).getDouble("confidence");
	            todas.append("- ").append(texto).append(" (confiança: ")
	                 .append(String.format("%.2f", confianca * 100)).append("%)\n");
	        }
	        return todas.toString();
	    } else {
	        return "Sem descrição encontrada.";
	    }

	}

	public static String enviarPromptParaAzureOpenAI(String urlimagem) throws Exception {
	    String apiKey = "ECqP8gZQ6VYs6MvWnbTOdvc7a34n2KXWDPEFY2jSQRKpVK5VluAIJQQJ99BFACHYHv6XJ3w3AAAAACOGaiHP";
	    String endpoint = "https://artur-mbdve4cu-eastus2.cognitiveservices.azure.com/";
	    String deploymentName = "gpt-4.1";

	    OpenAIClient client = new OpenAIClientBuilder()
	        .credential(new AzureKeyCredential(apiKey))
	        .endpoint(endpoint)
	        .buildClient();

	    // Mensagens adaptadas para análise de arte
	    List<ChatRequestMessage> chatMessages = Arrays.asList(
	        new ChatRequestSystemMessage("Você é um especialista em história da arte. Quando receber a descrição de uma imagem, identifique o estilo artístico, período histórico, técnicas utilizadas e artistas semelhantes. Seja detalhado e preciso."),
	        new ChatRequestUserMessage(urlimagem)
	    );

	    ChatCompletionsOptions chatCompletionsOptions = new ChatCompletionsOptions(chatMessages);
	    chatCompletionsOptions.setMaxTokens(800);
	    chatCompletionsOptions.setTemperature(0.8);
	    chatCompletionsOptions.setTopP(1.0);
	    chatCompletionsOptions.setFrequencyPenalty(0.0);
	    chatCompletionsOptions.setPresencePenalty(0.0);

	    ChatCompletions chatCompletions = client.getChatCompletions(deploymentName, chatCompletionsOptions);

	    return chatCompletions.getChoices().get(0).getMessage().getContent();
	}



    private static void salvarMensagem(Connection conn, String emailUsuario, int idOutro, String nomeOutro, String fotoOutro, JSONObject novaMensagem) throws Exception {
        String selectSql = "SELECT id, mensagens FROM mensagem WHERE usuario = ? AND nome = ?";
        PreparedStatement selectStmt = conn.prepareStatement(selectSql);
        selectStmt.setString(1, emailUsuario);
        selectStmt.setString(2, nomeOutro);
        ResultSet rs = selectStmt.executeQuery();

        JSONArray mensagens = new JSONArray();

        if (rs.next()) {
            String json = rs.getString("mensagens");
            if (json != null && !json.isEmpty()) {
                mensagens = new JSONArray(json);
            }
            mensagens.put(novaMensagem);

            String updateSql = "UPDATE mensagem SET mensagens = ?, updated_at = now() WHERE id = ?";
            PreparedStatement updateStmt = conn.prepareStatement(updateSql);
            updateStmt.setObject(1, mensagens.toString(), java.sql.Types.OTHER);
            updateStmt.setInt(2, rs.getInt("id"));
            updateStmt.executeUpdate();
        } else {
            mensagens.put(novaMensagem);
            String insertSql = "INSERT INTO mensagem (usuario, usuario_id, mensagens, nome, foto, created_at, updated_at) VALUES (?, ?, ?, ?, ?, now(), now())";
            PreparedStatement insertStmt = conn.prepareStatement(insertSql);
            insertStmt.setString(1, emailUsuario);
            insertStmt.setInt(2, idOutro);
            insertStmt.setObject(3, mensagens.toString(), java.sql.Types.OTHER);
            insertStmt.setString(4, nomeOutro);
            insertStmt.setString(5, fotoOutro);
            insertStmt.executeUpdate();
        }
    }

    // Função equivalente ao método getUserByEmail do JS
    private static JSONObject getUserByEmail(String email) throws Exception {
    String url = "jdbc:postgresql://pucmg5.postgres.database.azure.com:5432/postgres";
    String user = "adm"; // ou apenas "artur70152@ti270.postgres.database.azure.com" 
    	//private static final String password = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsIng1dCI6IkNOdjBPSTNSd3FsSEZFVm5hb01Bc2hDSDJYRSIsImtpZCI6IkNOdjBPSTNSd3FsSEZFVm5hb01Bc2hDSDJYRSJ9.eyJhdWQiOiJodHRwczovL29zc3JkYm1zLWFhZC5kYXRhYmFzZS53aW5kb3dzLm5ldCIsImlzcyI6Imh0dHBzOi8vc3RzLndpbmRvd3MubmV0Lzc4OTk2OTgzLTkwZTYtNGUxYS05MTU2LTNlYTQ3ZmYyNTQyNC8iLCJpYXQiOjE3NDg2MzgwNDQsIm5iZiI6MTc0ODYzODA0NCwiZXhwIjoxNzQ4NjQyNTU0LCJhY3IiOiIxIiwiYWlvIjoiQVpRQWEvOFpBQUFBZWhpNWVHVk5WM2NacmNmdWhZdWthU3Q3ZUFxWDVaenhmU25CNE1JSkoyeGpPcFIzZHVVRUo4SEhJVkVQRzZ0VmF3QTc2UmVZaTlUNmZNUUVWWm04WFpWeHpqOHRLMjZXQ2sxVkJ6QVp6SnBMaS9OOTd6dUdkTVZ0RUVZczBleUJtV3Arb0xhd1VzMTc3NUlVQkZ0OEtDRk1MZ3M2MnZNZ2tuVWFpT2N1SktrcG82OTlpWGd0azFZUzkxSENYL04zIiwiYWx0c2VjaWQiOiIxOmxpdmUuY29tOjAwMDM0MDAxMjQyNDIyRDMiLCJhbXIiOlsicHdkIiwibWZhIl0sImFwcGlkIjoiMDRiMDc3OTUtOGRkYi00NjFhLWJiZWUtMDJmOWUxYmY3YjQ2IiwiYXBwaWRhY3IiOiIwIiwiZW1haWwiOiJhcnR1cjcwMTUyQGdtYWlsLmNvbSIsImZhbWlseV9uYW1lIjoiZmVybmFuZGVzIiwiZ2l2ZW5fbmFtZSI6ImFydHVyIiwiZ3JvdXBzIjpbIjRmOGY5ZGQ2LTcyYzYtNDdjYy1iYmEyLTZhYmJkZWJlMzc3NyJdLCJpZHAiOiJsaXZlLmNvbSIsImlkdHlwIjoidXNlciIsImlwYWRkciI6IjI4MDQ6MTRjOjViYjE6OGIxMToyMjI0OjU0ZTM6YWIwNToxNTk3IiwibmFtZSI6ImFydHVyIGZlcm5hbmRlcyIsIm9pZCI6ImQ2NTZiOTRhLTQ1N2ItNDI2ZC1hYjVmLTUzMWMzNTE1OWQyMyIsInB1aWQiOiIxMDAzMjAwNDlBNkFGNDkwIiwicmgiOiIxLkFXTUJnMm1aZU9hUUdrNlJWajZrZl9KVUpGRFlQQkxmMmIxQWxOWEo4SHRfb2dQSUFhbGpBUS4iLCJzY3AiOiJ1c2VyX2ltcGVyc29uYXRpb24iLCJzaWQiOiIwMDUwZTcwOS0wODUyLThmOGMtNWI2Zi00NTM0YzM2NzdkZGIiLCJzdWIiOiJjQmdrSE5UTzJKTUs2ZXdVTFRDel9XU3VYV1I0Rm5VWjBtVEV5ZEUyVUJJIiwidGlkIjoiNzg5OTY5ODMtOTBlNi00ZTFhLTkxNTYtM2VhNDdmZjI1NDI0IiwidW5pcXVlX25hbWUiOiJsaXZlLmNvbSNhcnR1cjcwMTUyQGdtYWlsLmNvbSIsInV0aSI6Ikw5b2FfSTEydDBXOWhZNUp1cjRlQVEiLCJ2ZXIiOiIxLjAiLCJ4bXNfZnRkIjoiQlFRTkNPTlhTYWFvVVBvMWtzQTRUUDVxcHFkZnVndkNLeUNMdlN0X2FuUUJkWE51YjNKMGFDMWtjMjF6IiwieG1zX2lkcmVsIjoiMSAyNCJ9.VkcgQHboj6asRnAyJRRXHocp39vzd1tFhYIAoxJTVTL7S2-pit5MplccCyH1o-7TVIh-ojcX1jEcNcqZqH_imj1idoVOz-Hnt59beAgyVz0Txrn--zF8s6GcLb9aH2ZKjTF_HL0ShPAXWdQqPuvKg7ufBS4u-bhu18El42LcZyoxFbHOD_qUYfO-bcpYm1_DvpXJ22Zcqo-2pwGQrOgglRFh8XK2zxrXZANzzB1F5gEYt3ER0cw4Di-Gp0NlfeGTcJWBB2G8L88U_hJ0nq4eQHOcCCuPpqDw6uvpmqdXD-P8LbXasVg8HcZxbPO1HnS3wsUA-FtHSGuX0uPagQ0mkw"; 
    	 String password = "@Pucminas70152";
 //String password = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsIng1dCI6IkNOdjBPSTNSd3FsSEZFVm5hb01Bc2hDSDJYRSIsImtpZCI6IkNOdjBPSTNSd3FsSEZFVm5hb01Bc2hDSDJYRSJ9.eyJhdWQiOiJodHRwczovL29zc3JkYm1zLWFhZC5kYXRhYmFzZS53aW5kb3dzLm5ldCIsImlzcyI6Imh0dHBzOi8vc3RzLndpbmRvd3MubmV0Lzc4OTk2OTgzLTkwZTYtNGUxYS05MTU2LTNlYTQ3ZmYyNTQyNC8iLCJpYXQiOjE3NDg2MzgwNDQsIm5iZiI6MTc0ODYzODA0NCwiZXhwIjoxNzQ4NjQyNTU0LCJhY3IiOiIxIiwiYWlvIjoiQVpRQWEvOFpBQUFBZWhpNWVHVk5WM2NacmNmdWhZdWthU3Q3ZUFxWDVaenhmU25CNE1JSkoyeGpPcFIzZHVVRUo4SEhJVkVQRzZ0VmF3QTc2UmVZaTlUNmZNUUVWWm04WFpWeHpqOHRLMjZXQ2sxVkJ6QVp6SnBMaS9OOTd6dUdkTVZ0RUVZczBleUJtV3Arb0xhd1VzMTc3NUlVQkZ0OEtDRk1MZ3M2MnZNZ2tuVWFpT2N1SktrcG82OTlpWGd0azFZUzkxSENYL04zIiwiYWx0c2VjaWQiOiIxOmxpdmUuY29tOjAwMDM0MDAxMjQyNDIyRDMiLCJhbXIiOlsicHdkIiwibWZhIl0sImFwcGlkIjoiMDRiMDc3OTUtOGRkYi00NjFhLWJiZWUtMDJmOWUxYmY3YjQ2IiwiYXBwaWRhY3IiOiIwIiwiZW1haWwiOiJhcnR1cjcwMTUyQGdtYWlsLmNvbSIsImZhbWlseV9uYW1lIjoiZmVybmFuZGVzIiwiZ2l2ZW5fbmFtZSI6ImFydHVyIiwiZ3JvdXBzIjpbIjRmOGY5ZGQ2LTcyYzYtNDdjYy1iYmEyLTZhYmJkZWJlMzc3NyJdLCJpZHAiOiJsaXZlLmNvbSIsImlkdHlwIjoidXNlciIsImlwYWRkciI6IjI4MDQ6MTRjOjViYjE6OGIxMToyMjI0OjU0ZTM6YWIwNToxNTk3IiwibmFtZSI6ImFydHVyIGZlcm5hbmRlcyIsIm9pZCI6ImQ2NTZiOTRhLTQ1N2ItNDI2ZC1hYjVmLTUzMWMzNTE1OWQyMyIsInB1aWQiOiIxMDAzMjAwNDlBNkFGNDkwIiwicmgiOiIxLkFXTUJnMm1aZU9hUUdrNlJWajZrZl9KVUpGRFlQQkxmMmIxQWxOWEo4SHRfb2dQSUFhbGpBUS4iLCJzY3AiOiJ1c2VyX2ltcGVyc29uYXRpb24iLCJzaWQiOiIwMDUwZTcwOS0wODUyLThmOGMtNWI2Zi00NTM0YzM2NzdkZGIiLCJzdWIiOiJjQmdrSE5UTzJKTUs2ZXdVTFRDel9XU3VYV1I0Rm5VWjBtVEV5ZEUyVUJJIiwidGlkIjoiNzg5OTY5ODMtOTBlNi00ZTFhLTkxNTYtM2VhNDdmZjI1NDI0IiwidW5pcXVlX25hbWUiOiJsaXZlLmNvbSNhcnR1cjcwMTUyQGdtYWlsLmNvbSIsInV0aSI6Ikw5b2FfSTEydDBXOWhZNUp1cjRlQVEiLCJ2ZXIiOiIxLjAiLCJ4bXNfZnRkIjoiQlFRTkNPTlhTYWFvVVBvMWtzQTRUUDVxcHFkZnVndkNLeUNMdlN0X2FuUUJkWE51YjNKMGFDMWtjMjF6IiwieG1zX2lkcmVsIjoiMSAyNCJ9.VkcgQHboj6asRnAyJRRXHocp39vzd1tFhYIAoxJTVTL7S2-pit5MplccCyH1o-7TVIh-ojcX1jEcNcqZqH_imj1idoVOz-Hnt59beAgyVz0Txrn--zF8s6GcLb9aH2ZKjTF_HL0ShPAXWdQqPuvKg7ufBS4u-bhu18El42LcZyoxFbHOD_qUYfO-bcpYm1_DvpXJ22Zcqo-2pwGQrOgglRFh8XK2zxrXZANzzB1F5gEYt3ER0cw4Di-Gp0NlfeGTcJWBB2G8L88U_hJ0nq4eQHOcCCuPpqDw6uvpmqdXD-P8LbXasVg8HcZxbPO1HnS3wsUA-FtHSGuX0uPagQ0mkw"; 
 //String password = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsIng1dCI6IkNOdjBPSTNSd3FsSEZFVm5hb01Bc2hDSDJYRSIsImtpZCI6IkNOdjBPSTNSd3FsSEZFVm5hb01Bc2hDSDJYRSJ9.eyJhdWQiOiJodHRwczovL29zc3JkYm1zLWFhZC5kYXRhYmFzZS53aW5kb3dzLm5ldCIsImlzcyI6Imh0dHBzOi8vc3RzLndpbmRvd3MubmV0Lzc4OTk2OTgzLTkwZTYtNGUxYS05MTU2LTNlYTQ3ZmYyNTQyNC8iLCJpYXQiOjE3NDg2MzgwNDQsIm5iZiI6MTc0ODYzODA0NCwiZXhwIjoxNzQ4NjQyNTU0LCJhY3IiOiIxIiwiYWlvIjoiQVpRQWEvOFpBQUFBZWhpNWVHVk5WM2NacmNmdWhZdWthU3Q3ZUFxWDVaenhmU25CNE1JSkoyeGpPcFIzZHVVRUo4SEhJVkVQRzZ0VmF3QTc2UmVZaTlUNmZNUUVWWm04WFpWeHpqOHRLMjZXQ2sxVkJ6QVp6SnBMaS9OOTd6dUdkTVZ0RUVZczBleUJtV3Arb0xhd1VzMTc3NUlVQkZ0OEtDRk1MZ3M2MnZNZ2tuVWFpT2N1SktrcG82OTlpWGd0azFZUzkxSENYL04zIiwiYWx0c2VjaWQiOiIxOmxpdmUuY29tOjAwMDM0MDAxMjQyNDIyRDMiLCJhbXIiOlsicHdkIiwibWZhIl0sImFwcGlkIjoiMDRiMDc3OTUtOGRkYi00NjFhLWJiZWUtMDJmOWUxYmY3YjQ2IiwiYXBwaWRhY3IiOiIwIiwiZW1haWwiOiJhcnR1cjcwMTUyQGdtYWlsLmNvbSIsImZhbWlseV9uYW1lIjoiZmVybmFuZGVzIiwiZ2l2ZW5fbmFtZSI6ImFydHVyIiwiZ3JvdXBzIjpbIjRmOGY5ZGQ2LTcyYzYtNDdjYy1iYmEyLTZhYmJkZWJlMzc3NyJdLCJpZHAiOiJsaXZlLmNvbSIsImlkdHlwIjoidXNlciIsImlwYWRkciI6IjI4MDQ6MTRjOjViYjE6OGIxMToyMjI0OjU0ZTM6YWIwNToxNTk3IiwibmFtZSI6ImFydHVyIGZlcm5hbmRlcyIsIm9pZCI6ImQ2NTZiOTRhLTQ1N2ItNDI2ZC1hYjVmLTUzMWMzNTE1OWQyMyIsInB1aWQiOiIxMDAzMjAwNDlBNkFGNDkwIiwicmgiOiIxLkFXTUJnMm1aZU9hUUdrNlJWajZrZl9KVUpGRFlQQkxmMmIxQWxOWEo4SHRfb2dQSUFhbGpBUS4iLCJzY3AiOiJ1c2VyX2ltcGVyc29uYXRpb24iLCJzaWQiOiIwMDUwZTcwOS0wODUyLThmOGMtNWI2Zi00NTM0YzM2NzdkZGIiLCJzdWIiOiJjQmdrSE5UTzJKTUs2ZXdVTFRDel9XU3VYV1I0Rm5VWjBtVEV5ZEUyVUJJIiwidGlkIjoiNzg5OTY5ODMtOTBlNi00ZTFhLTkxNTYtM2VhNDdmZjI1NDI0IiwidW5pcXVlX25hbWUiOiJsaXZlLmNvbSNhcnR1cjcwMTUyQGdtYWlsLmNvbSIsInV0aSI6Ikw5b2FfSTEydDBXOWhZNUp1cjRlQVEiLCJ2ZXIiOiIxLjAiLCJ4bXNfZnRkIjoiQlFRTkNPTlhTYWFvVVBvMWtzQTRUUDVxcHFkZnVndkNLeUNMdlN0X2FuUUJkWE51YjNKMGFDMWtjMjF6IiwieG1zX2lkcmVsIjoiMSAyNCJ9.VkcgQHboj6asRnAyJRRXHocp39vzd1tFhYIAoxJTVTL7S2-pit5MplccCyH1o-7TVIh-ojcX1jEcNcqZqH_imj1idoVOz-Hnt59beAgyVz0Txrn--zF8s6GcLb9aH2ZKjTF_HL0ShPAXWdQqPuvKg7ufBS4u-bhu18El42LcZyoxFbHOD_qUYfO-bcpYm1_DvpXJ22Zcqo-2pwGQrOgglRFh8XK2zxrXZANzzB1F5gEYt3ER0cw4Di-Gp0NlfeGTcJWBB2G8L88U_hJ0nq4eQHOcCCuPpqDw6uvpmqdXD-P8LbXasVg8HcZxbPO1HnS3wsUA-FtHSGuX0uPagQ0mkw";

        Class.forName("org.postgresql.Driver");

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            String sql = "SELECT name, normal, doacoes, email, senha, foto FROM usuario WHERE email = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, email);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        JSONObject obj = new JSONObject();
                        obj.put("name", rs.getString("name"));
                        obj.put("normal", rs.getBoolean("normal"));
                        obj.put("doacoes", rs.getDouble("doacoes"));
                        obj.put("email", rs.getString("email"));
                        obj.put("senha", rs.getString("senha"));
                        obj.put("foto", rs.getString("foto"));
                        return obj;
                    }
                }
            }
        }
        return null;  // Não encontrou usuário
    }
    private static JSONArray getArtesByEmail(String email) throws Exception {
        String url = "jdbc:postgresql://pucmg5.postgres.database.azure.com:5432/postgres";
        String user = "adm"; // ou apenas "artur70152@ti270.postgres.database.azure.com" 
        	//private static final String password = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsIng1dCI6IkNOdjBPSTNSd3FsSEZFVm5hb01Bc2hDSDJYRSIsImtpZCI6IkNOdjBPSTNSd3FsSEZFVm5hb01Bc2hDSDJYRSJ9.eyJhdWQiOiJodHRwczovL29zc3JkYm1zLWFhZC5kYXRhYmFzZS53aW5kb3dzLm5ldCIsImlzcyI6Imh0dHBzOi8vc3RzLndpbmRvd3MubmV0Lzc4OTk2OTgzLTkwZTYtNGUxYS05MTU2LTNlYTQ3ZmYyNTQyNC8iLCJpYXQiOjE3NDg2MzgwNDQsIm5iZiI6MTc0ODYzODA0NCwiZXhwIjoxNzQ4NjQyNTU0LCJhY3IiOiIxIiwiYWlvIjoiQVpRQWEvOFpBQUFBZWhpNWVHVk5WM2NacmNmdWhZdWthU3Q3ZUFxWDVaenhmU25CNE1JSkoyeGpPcFIzZHVVRUo4SEhJVkVQRzZ0VmF3QTc2UmVZaTlUNmZNUUVWWm04WFpWeHpqOHRLMjZXQ2sxVkJ6QVp6SnBMaS9OOTd6dUdkTVZ0RUVZczBleUJtV3Arb0xhd1VzMTc3NUlVQkZ0OEtDRk1MZ3M2MnZNZ2tuVWFpT2N1SktrcG82OTlpWGd0azFZUzkxSENYL04zIiwiYWx0c2VjaWQiOiIxOmxpdmUuY29tOjAwMDM0MDAxMjQyNDIyRDMiLCJhbXIiOlsicHdkIiwibWZhIl0sImFwcGlkIjoiMDRiMDc3OTUtOGRkYi00NjFhLWJiZWUtMDJmOWUxYmY3YjQ2IiwiYXBwaWRhY3IiOiIwIiwiZW1haWwiOiJhcnR1cjcwMTUyQGdtYWlsLmNvbSIsImZhbWlseV9uYW1lIjoiZmVybmFuZGVzIiwiZ2l2ZW5fbmFtZSI6ImFydHVyIiwiZ3JvdXBzIjpbIjRmOGY5ZGQ2LTcyYzYtNDdjYy1iYmEyLTZhYmJkZWJlMzc3NyJdLCJpZHAiOiJsaXZlLmNvbSIsImlkdHlwIjoidXNlciIsImlwYWRkciI6IjI4MDQ6MTRjOjViYjE6OGIxMToyMjI0OjU0ZTM6YWIwNToxNTk3IiwibmFtZSI6ImFydHVyIGZlcm5hbmRlcyIsIm9pZCI6ImQ2NTZiOTRhLTQ1N2ItNDI2ZC1hYjVmLTUzMWMzNTE1OWQyMyIsInB1aWQiOiIxMDAzMjAwNDlBNkFGNDkwIiwicmgiOiIxLkFXTUJnMm1aZU9hUUdrNlJWajZrZl9KVUpGRFlQQkxmMmIxQWxOWEo4SHRfb2dQSUFhbGpBUS4iLCJzY3AiOiJ1c2VyX2ltcGVyc29uYXRpb24iLCJzaWQiOiIwMDUwZTcwOS0wODUyLThmOGMtNWI2Zi00NTM0YzM2NzdkZGIiLCJzdWIiOiJjQmdrSE5UTzJKTUs2ZXdVTFRDel9XU3VYV1I0Rm5VWjBtVEV5ZEUyVUJJIiwidGlkIjoiNzg5OTY5ODMtOTBlNi00ZTFhLTkxNTYtM2VhNDdmZjI1NDI0IiwidW5pcXVlX25hbWUiOiJsaXZlLmNvbSNhcnR1cjcwMTUyQGdtYWlsLmNvbSIsInV0aSI6Ikw5b2FfSTEydDBXOWhZNUp1cjRlQVEiLCJ2ZXIiOiIxLjAiLCJ4bXNfZnRkIjoiQlFRTkNPTlhTYWFvVVBvMWtzQTRUUDVxcHFkZnVndkNLeUNMdlN0X2FuUUJkWE51YjNKMGFDMWtjMjF6IiwieG1zX2lkcmVsIjoiMSAyNCJ9.VkcgQHboj6asRnAyJRRXHocp39vzd1tFhYIAoxJTVTL7S2-pit5MplccCyH1o-7TVIh-ojcX1jEcNcqZqH_imj1idoVOz-Hnt59beAgyVz0Txrn--zF8s6GcLb9aH2ZKjTF_HL0ShPAXWdQqPuvKg7ufBS4u-bhu18El42LcZyoxFbHOD_qUYfO-bcpYm1_DvpXJ22Zcqo-2pwGQrOgglRFh8XK2zxrXZANzzB1F5gEYt3ER0cw4Di-Gp0NlfeGTcJWBB2G8L88U_hJ0nq4eQHOcCCuPpqDw6uvpmqdXD-P8LbXasVg8HcZxbPO1HnS3wsUA-FtHSGuX0uPagQ0mkw"; 
        	 String password = "@Pucminas70152";
        Class.forName("org.postgresql.Driver");

        JSONArray artes = new JSONArray();

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            // 1. Buscar usuário
            String userSql = "SELECT id FROM usuario WHERE email = ?";
            try (PreparedStatement userStmt = conn.prepareStatement(userSql)) {
                userStmt.setString(1, email);
                try (ResultSet rs = userStmt.executeQuery()) {
                    if (!rs.next()) {
                        return null;  // Usuário não encontrado
                    }
                    int usuarioId = rs.getInt("id");

                    // 2. Buscar todas as artes do usuário
                    String sql = "SELECT id, arte FROM arte WHERE usuario_id = ?";
                    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                        stmt.setInt(1, usuarioId);
                        try (ResultSet rsArtes = stmt.executeQuery()) {
                            while (rsArtes.next()) {
                                int arteId = rsArtes.getInt("id");
                                String artePath = rsArtes.getString("arte");

                                JSONObject arte = new JSONObject();
                                arte.put("id", arteId);
                                arte.put("arte", artePath);

                                // 3. Buscar comentários desta arte
                                JSONArray comentarios = new JSONArray();

                                String sqlComentario = "SELECT usuario, texto FROM comentario_arte WHERE arte_id = ?";
                                try (PreparedStatement stmtComentario = conn.prepareStatement(sqlComentario)) {
                                    stmtComentario.setInt(1, arteId);
                                    try (ResultSet rsComentarios = stmtComentario.executeQuery()) {
                                        while (rsComentarios.next()) {
                                            JSONObject comentario = new JSONObject();
                                            comentario.put("usuario", rsComentarios.getString("usuario"));
                                            comentario.put("texto", rsComentarios.getString("texto"));
                                            comentarios.put(comentario);
                                        }
                                    }
                                }

                                // 4. Adiciona os comentários à arte
                                arte.put("comentarios", comentarios);

                                // 5. Adiciona a arte à lista
                                artes.put(arte);
                            }
                        }
                    }
                }
            }
        }
        return artes;
    }


  
    private static void popularArtes() throws Exception {
        String url = "jdbc:postgresql://pucmg5.postgres.database.azure.com:5432/postgres";
        String user = "adm"; // ou apenas "artur70152@ti270.postgres.database.azure.com" 
        	//private static final String password = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsIng1dCI6IkNOdjBPSTNSd3FsSEZFVm5hb01Bc2hDSDJYRSIsImtpZCI6IkNOdjBPSTNSd3FsSEZFVm5hb01Bc2hDSDJYRSJ9.eyJhdWQiOiJodHRwczovL29zc3JkYm1zLWFhZC5kYXRhYmFzZS53aW5kb3dzLm5ldCIsImlzcyI6Imh0dHBzOi8vc3RzLndpbmRvd3MubmV0Lzc4OTk2OTgzLTkwZTYtNGUxYS05MTU2LTNlYTQ3ZmYyNTQyNC8iLCJpYXQiOjE3NDg2MzgwNDQsIm5iZiI6MTc0ODYzODA0NCwiZXhwIjoxNzQ4NjQyNTU0LCJhY3IiOiIxIiwiYWlvIjoiQVpRQWEvOFpBQUFBZWhpNWVHVk5WM2NacmNmdWhZdWthU3Q3ZUFxWDVaenhmU25CNE1JSkoyeGpPcFIzZHVVRUo4SEhJVkVQRzZ0VmF3QTc2UmVZaTlUNmZNUUVWWm04WFpWeHpqOHRLMjZXQ2sxVkJ6QVp6SnBMaS9OOTd6dUdkTVZ0RUVZczBleUJtV3Arb0xhd1VzMTc3NUlVQkZ0OEtDRk1MZ3M2MnZNZ2tuVWFpT2N1SktrcG82OTlpWGd0azFZUzkxSENYL04zIiwiYWx0c2VjaWQiOiIxOmxpdmUuY29tOjAwMDM0MDAxMjQyNDIyRDMiLCJhbXIiOlsicHdkIiwibWZhIl0sImFwcGlkIjoiMDRiMDc3OTUtOGRkYi00NjFhLWJiZWUtMDJmOWUxYmY3YjQ2IiwiYXBwaWRhY3IiOiIwIiwiZW1haWwiOiJhcnR1cjcwMTUyQGdtYWlsLmNvbSIsImZhbWlseV9uYW1lIjoiZmVybmFuZGVzIiwiZ2l2ZW5fbmFtZSI6ImFydHVyIiwiZ3JvdXBzIjpbIjRmOGY5ZGQ2LTcyYzYtNDdjYy1iYmEyLTZhYmJkZWJlMzc3NyJdLCJpZHAiOiJsaXZlLmNvbSIsImlkdHlwIjoidXNlciIsImlwYWRkciI6IjI4MDQ6MTRjOjViYjE6OGIxMToyMjI0OjU0ZTM6YWIwNToxNTk3IiwibmFtZSI6ImFydHVyIGZlcm5hbmRlcyIsIm9pZCI6ImQ2NTZiOTRhLTQ1N2ItNDI2ZC1hYjVmLTUzMWMzNTE1OWQyMyIsInB1aWQiOiIxMDAzMjAwNDlBNkFGNDkwIiwicmgiOiIxLkFXTUJnMm1aZU9hUUdrNlJWajZrZl9KVUpGRFlQQkxmMmIxQWxOWEo4SHRfb2dQSUFhbGpBUS4iLCJzY3AiOiJ1c2VyX2ltcGVyc29uYXRpb24iLCJzaWQiOiIwMDUwZTcwOS0wODUyLThmOGMtNWI2Zi00NTM0YzM2NzdkZGIiLCJzdWIiOiJjQmdrSE5UTzJKTUs2ZXdVTFRDel9XU3VYV1I0Rm5VWjBtVEV5ZEUyVUJJIiwidGlkIjoiNzg5OTY5ODMtOTBlNi00ZTFhLTkxNTYtM2VhNDdmZjI1NDI0IiwidW5pcXVlX25hbWUiOiJsaXZlLmNvbSNhcnR1cjcwMTUyQGdtYWlsLmNvbSIsInV0aSI6Ikw5b2FfSTEydDBXOWhZNUp1cjRlQVEiLCJ2ZXIiOiIxLjAiLCJ4bXNfZnRkIjoiQlFRTkNPTlhTYWFvVVBvMWtzQTRUUDVxcHFkZnVndkNLeUNMdlN0X2FuUUJkWE51YjNKMGFDMWtjMjF6IiwieG1zX2lkcmVsIjoiMSAyNCJ9.VkcgQHboj6asRnAyJRRXHocp39vzd1tFhYIAoxJTVTL7S2-pit5MplccCyH1o-7TVIh-ojcX1jEcNcqZqH_imj1idoVOz-Hnt59beAgyVz0Txrn--zF8s6GcLb9aH2ZKjTF_HL0ShPAXWdQqPuvKg7ufBS4u-bhu18El42LcZyoxFbHOD_qUYfO-bcpYm1_DvpXJ22Zcqo-2pwGQrOgglRFh8XK2zxrXZANzzB1F5gEYt3ER0cw4Di-Gp0NlfeGTcJWBB2G8L88U_hJ0nq4eQHOcCCuPpqDw6uvpmqdXD-P8LbXasVg8HcZxbPO1HnS3wsUA-FtHSGuX0uPagQ0mkw"; 
        	 String password = "@Pucminas70152";

        Class.forName("org.postgresql.Driver");

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            String userSql = "SELECT id, name FROM usuario";
            try (PreparedStatement userStmt = conn.prepareStatement(userSql);
                 ResultSet users = userStmt.executeQuery()) {

                String[] imagens = {"/images/arte1.jpg", "/images/arte2.jpg", "/images/arte3.jpg"};

                String insertSql = "INSERT INTO arte (usuario_id, arte, name, curtidas) VALUES (?, ?, ?, ?)";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    while (users.next()) {
                        int usuarioId = users.getInt("id");
                        String usuarioName = users.getString("name");
                        for (int i = 0; i < 3; i++) {
                            insertStmt.setInt(1, usuarioId);
                            insertStmt.setString(2, imagens[i % imagens.length]);
                            insertStmt.setString(3, "Arte " + (i + 1) + " de " + usuarioName);
                            insertStmt.setInt(4, 0);
                            insertStmt.addBatch();
                        }
                    }
                    insertStmt.executeBatch();
                }
            }
        }
    }
    private static JSONObject addArte(String email, String arte, String name, int curtidas) throws Exception {
        String url = "jdbc:postgresql://pucmg5.postgres.database.azure.com:5432/postgres";
        String user = "adm"; // ou apenas "artur70152@ti270.postgres.database.azure.com" 
        	//private static final String password = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsIng1dCI6IkNOdjBPSTNSd3FsSEZFVm5hb01Bc2hDSDJYRSIsImtpZCI6IkNOdjBPSTNSd3FsSEZFVm5hb01Bc2hDSDJYRSJ9.eyJhdWQiOiJodHRwczovL29zc3JkYm1zLWFhZC5kYXRhYmFzZS53aW5kb3dzLm5ldCIsImlzcyI6Imh0dHBzOi8vc3RzLndpbmRvd3MubmV0Lzc4OTk2OTgzLTkwZTYtNGUxYS05MTU2LTNlYTQ3ZmYyNTQyNC8iLCJpYXQiOjE3NDg2MzgwNDQsIm5iZiI6MTc0ODYzODA0NCwiZXhwIjoxNzQ4NjQyNTU0LCJhY3IiOiIxIiwiYWlvIjoiQVpRQWEvOFpBQUFBZWhpNWVHVk5WM2NacmNmdWhZdWthU3Q3ZUFxWDVaenhmU25CNE1JSkoyeGpPcFIzZHVVRUo4SEhJVkVQRzZ0VmF3QTc2UmVZaTlUNmZNUUVWWm04WFpWeHpqOHRLMjZXQ2sxVkJ6QVp6SnBMaS9OOTd6dUdkTVZ0RUVZczBleUJtV3Arb0xhd1VzMTc3NUlVQkZ0OEtDRk1MZ3M2MnZNZ2tuVWFpT2N1SktrcG82OTlpWGd0azFZUzkxSENYL04zIiwiYWx0c2VjaWQiOiIxOmxpdmUuY29tOjAwMDM0MDAxMjQyNDIyRDMiLCJhbXIiOlsicHdkIiwibWZhIl0sImFwcGlkIjoiMDRiMDc3OTUtOGRkYi00NjFhLWJiZWUtMDJmOWUxYmY3YjQ2IiwiYXBwaWRhY3IiOiIwIiwiZW1haWwiOiJhcnR1cjcwMTUyQGdtYWlsLmNvbSIsImZhbWlseV9uYW1lIjoiZmVybmFuZGVzIiwiZ2l2ZW5fbmFtZSI6ImFydHVyIiwiZ3JvdXBzIjpbIjRmOGY5ZGQ2LTcyYzYtNDdjYy1iYmEyLTZhYmJkZWJlMzc3NyJdLCJpZHAiOiJsaXZlLmNvbSIsImlkdHlwIjoidXNlciIsImlwYWRkciI6IjI4MDQ6MTRjOjViYjE6OGIxMToyMjI0OjU0ZTM6YWIwNToxNTk3IiwibmFtZSI6ImFydHVyIGZlcm5hbmRlcyIsIm9pZCI6ImQ2NTZiOTRhLTQ1N2ItNDI2ZC1hYjVmLTUzMWMzNTE1OWQyMyIsInB1aWQiOiIxMDAzMjAwNDlBNkFGNDkwIiwicmgiOiIxLkFXTUJnMm1aZU9hUUdrNlJWajZrZl9KVUpGRFlQQkxmMmIxQWxOWEo4SHRfb2dQSUFhbGpBUS4iLCJzY3AiOiJ1c2VyX2ltcGVyc29uYXRpb24iLCJzaWQiOiIwMDUwZTcwOS0wODUyLThmOGMtNWI2Zi00NTM0YzM2NzdkZGIiLCJzdWIiOiJjQmdrSE5UTzJKTUs2ZXdVTFRDel9XU3VYV1I0Rm5VWjBtVEV5ZEUyVUJJIiwidGlkIjoiNzg5OTY5ODMtOTBlNi00ZTFhLTkxNTYtM2VhNDdmZjI1NDI0IiwidW5pcXVlX25hbWUiOiJsaXZlLmNvbSNhcnR1cjcwMTUyQGdtYWlsLmNvbSIsInV0aSI6Ikw5b2FfSTEydDBXOWhZNUp1cjRlQVEiLCJ2ZXIiOiIxLjAiLCJ4bXNfZnRkIjoiQlFRTkNPTlhTYWFvVVBvMWtzQTRUUDVxcHFkZnVndkNLeUNMdlN0X2FuUUJkWE51YjNKMGFDMWtjMjF6IiwieG1zX2lkcmVsIjoiMSAyNCJ9.VkcgQHboj6asRnAyJRRXHocp39vzd1tFhYIAoxJTVTL7S2-pit5MplccCyH1o-7TVIh-ojcX1jEcNcqZqH_imj1idoVOz-Hnt59beAgyVz0Txrn--zF8s6GcLb9aH2ZKjTF_HL0ShPAXWdQqPuvKg7ufBS4u-bhu18El42LcZyoxFbHOD_qUYfO-bcpYm1_DvpXJ22Zcqo-2pwGQrOgglRFh8XK2zxrXZANzzB1F5gEYt3ER0cw4Di-Gp0NlfeGTcJWBB2G8L88U_hJ0nq4eQHOcCCuPpqDw6uvpmqdXD-P8LbXasVg8HcZxbPO1HnS3wsUA-FtHSGuX0uPagQ0mkw"; 
        	 String password = "@Pucminas70152";
        Class.forName("org.postgresql.Driver");

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            String userSql = "SELECT id FROM usuario WHERE email = ?";
            try (PreparedStatement userStmt = conn.prepareStatement(userSql)) {
                userStmt.setString(1, email);
                try (ResultSet rs = userStmt.executeQuery()) {
                    if (!rs.next()) {
                        return null;  // Usuário não encontrado
                    }
                    int usuarioId = rs.getInt("id");

                    String insertSql = "INSERT INTO arte (usuario_id, arte, name, curtidas) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                        insertStmt.setInt(1, usuarioId);
                        insertStmt.setString(2, arte);
                        insertStmt.setString(3, name);
                        insertStmt.setInt(4, curtidas);
                        insertStmt.executeUpdate();
                    }
                }
            }
        }
        return new JSONObject().put("message", "Arte adicionada com sucesso.");
    }
    private static JSONObject updateCurtidas(String email, int arteId, String usuario) throws Exception {
        String url = "jdbc:postgresql://pucmg5.postgres.database.azure.com:5432/postgres";
        String user = "adm"; // ou apenas "artur70152@ti270.postgres.database.azure.com" 
        	//private static final String password = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsIng1dCI6IkNOdjBPSTNSd3FsSEZFVm5hb01Bc2hDSDJYRSIsImtpZCI6IkNOdjBPSTNSd3FsSEZFVm5hb01Bc2hDSDJYRSJ9.eyJhdWQiOiJodHRwczovL29zc3JkYm1zLWFhZC5kYXRhYmFzZS53aW5kb3dzLm5ldCIsImlzcyI6Imh0dHBzOi8vc3RzLndpbmRvd3MubmV0Lzc4OTk2OTgzLTkwZTYtNGUxYS05MTU2LTNlYTQ3ZmYyNTQyNC8iLCJpYXQiOjE3NDg2MzgwNDQsIm5iZiI6MTc0ODYzODA0NCwiZXhwIjoxNzQ4NjQyNTU0LCJhY3IiOiIxIiwiYWlvIjoiQVpRQWEvOFpBQUFBZWhpNWVHVk5WM2NacmNmdWhZdWthU3Q3ZUFxWDVaenhmU25CNE1JSkoyeGpPcFIzZHVVRUo4SEhJVkVQRzZ0VmF3QTc2UmVZaTlUNmZNUUVWWm04WFpWeHpqOHRLMjZXQ2sxVkJ6QVp6SnBMaS9OOTd6dUdkTVZ0RUVZczBleUJtV3Arb0xhd1VzMTc3NUlVQkZ0OEtDRk1MZ3M2MnZNZ2tuVWFpT2N1SktrcG82OTlpWGd0azFZUzkxSENYL04zIiwiYWx0c2VjaWQiOiIxOmxpdmUuY29tOjAwMDM0MDAxMjQyNDIyRDMiLCJhbXIiOlsicHdkIiwibWZhIl0sImFwcGlkIjoiMDRiMDc3OTUtOGRkYi00NjFhLWJiZWUtMDJmOWUxYmY3YjQ2IiwiYXBwaWRhY3IiOiIwIiwiZW1haWwiOiJhcnR1cjcwMTUyQGdtYWlsLmNvbSIsImZhbWlseV9uYW1lIjoiZmVybmFuZGVzIiwiZ2l2ZW5fbmFtZSI6ImFydHVyIiwiZ3JvdXBzIjpbIjRmOGY5ZGQ2LTcyYzYtNDdjYy1iYmEyLTZhYmJkZWJlMzc3NyJdLCJpZHAiOiJsaXZlLmNvbSIsImlkdHlwIjoidXNlciIsImlwYWRkciI6IjI4MDQ6MTRjOjViYjE6OGIxMToyMjI0OjU0ZTM6YWIwNToxNTk3IiwibmFtZSI6ImFydHVyIGZlcm5hbmRlcyIsIm9pZCI6ImQ2NTZiOTRhLTQ1N2ItNDI2ZC1hYjVmLTUzMWMzNTE1OWQyMyIsInB1aWQiOiIxMDAzMjAwNDlBNkFGNDkwIiwicmgiOiIxLkFXTUJnMm1aZU9hUUdrNlJWajZrZl9KVUpGRFlQQkxmMmIxQWxOWEo4SHRfb2dQSUFhbGpBUS4iLCJzY3AiOiJ1c2VyX2ltcGVyc29uYXRpb24iLCJzaWQiOiIwMDUwZTcwOS0wODUyLThmOGMtNWI2Zi00NTM0YzM2NzdkZGIiLCJzdWIiOiJjQmdrSE5UTzJKTUs2ZXdVTFRDel9XU3VYV1I0Rm5VWjBtVEV5ZEUyVUJJIiwidGlkIjoiNzg5OTY5ODMtOTBlNi00ZTFhLTkxNTYtM2VhNDdmZjI1NDI0IiwidW5pcXVlX25hbWUiOiJsaXZlLmNvbSNhcnR1cjcwMTUyQGdtYWlsLmNvbSIsInV0aSI6Ikw5b2FfSTEydDBXOWhZNUp1cjRlQVEiLCJ2ZXIiOiIxLjAiLCJ4bXNfZnRkIjoiQlFRTkNPTlhTYWFvVVBvMWtzQTRUUDVxcHFkZnVndkNLeUNMdlN0X2FuUUJkWE51YjNKMGFDMWtjMjF6IiwieG1zX2lkcmVsIjoiMSAyNCJ9.VkcgQHboj6asRnAyJRRXHocp39vzd1tFhYIAoxJTVTL7S2-pit5MplccCyH1o-7TVIh-ojcX1jEcNcqZqH_imj1idoVOz-Hnt59beAgyVz0Txrn--zF8s6GcLb9aH2ZKjTF_HL0ShPAXWdQqPuvKg7ufBS4u-bhu18El42LcZyoxFbHOD_qUYfO-bcpYm1_DvpXJ22Zcqo-2pwGQrOgglRFh8XK2zxrXZANzzB1F5gEYt3ER0cw4Di-Gp0NlfeGTcJWBB2G8L88U_hJ0nq4eQHOcCCuPpqDw6uvpmqdXD-P8LbXasVg8HcZxbPO1HnS3wsUA-FtHSGuX0uPagQ0mkw"; 
        	 String password = "@Pucminas70152";

        Class.forName("org.postgresql.Driver");

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            String userSql = "SELECT id FROM usuario WHERE email = ?";
            try (PreparedStatement userStmt = conn.prepareStatement(userSql)) {
                userStmt.setString(1, email);
                try (ResultSet rs = userStmt.executeQuery()) {
                    if (!rs.next()) {
                        return new JSONObject().put("error", "Usuário não encontrado");
                    }
                }
            }

            String checkSql = "SELECT * FROM curtidaarte WHERE arte_id = ? AND usuario = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, arteId);
                checkStmt.setString(2, usuario);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        // Descurtir
                        String delSql = "DELETE FROM curtidaarte WHERE arte_id = ? AND usuario = ?";
                        try (PreparedStatement delStmt = conn.prepareStatement(delSql)) {
                            delStmt.setInt(1, arteId);
                            delStmt.setString(2, usuario);
                            delStmt.executeUpdate();
                        }

                        String updSql = "UPDATE arte SET curtidas = GREATEST(curtidas - 1, 0) WHERE id = ?";
                        try (PreparedStatement updStmt = conn.prepareStatement(updSql)) {
                            updStmt.setInt(1, arteId);
                            updStmt.executeUpdate();
                        }

                        return new JSONObject().put("message", "Descurtido");
                    } else {
                        // Curtir
                        String insSql = "INSERT INTO curtidaarte (arte_id, usuario) VALUES (?, ?)";
                        try (PreparedStatement insStmt = conn.prepareStatement(insSql)) {
                            insStmt.setInt(1, arteId);
                            insStmt.setString(2, usuario);
                            insStmt.executeUpdate();
                        }

                        String updSql = "UPDATE arte SET curtidas = curtidas + 1 WHERE id = ?";
                        try (PreparedStatement updStmt = conn.prepareStatement(updSql)) {
                            updStmt.setInt(1, arteId);
                            updStmt.executeUpdate();
                        }

                        return new JSONObject().put("message", "Curtido");
                    }
                }
            }
        }
    }
    private static JSONObject removeArte(int arteId) throws Exception {
        String url = "jdbc:postgresql://pucmg5.postgres.database.azure.com:5432/postgres";
        String user = "adm"; // ou apenas "artur70152@ti270.postgres.database.azure.com" 
        	//private static final String password = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsIng1dCI6IkNOdjBPSTNSd3FsSEZFVm5hb01Bc2hDSDJYRSIsImtpZCI6IkNOdjBPSTNSd3FsSEZFVm5hb01Bc2hDSDJYRSJ9.eyJhdWQiOiJodHRwczovL29zc3JkYm1zLWFhZC5kYXRhYmFzZS53aW5kb3dzLm5ldCIsImlzcyI6Imh0dHBzOi8vc3RzLndpbmRvd3MubmV0Lzc4OTk2OTgzLTkwZTYtNGUxYS05MTU2LTNlYTQ3ZmYyNTQyNC8iLCJpYXQiOjE3NDg2MzgwNDQsIm5iZiI6MTc0ODYzODA0NCwiZXhwIjoxNzQ4NjQyNTU0LCJhY3IiOiIxIiwiYWlvIjoiQVpRQWEvOFpBQUFBZWhpNWVHVk5WM2NacmNmdWhZdWthU3Q3ZUFxWDVaenhmU25CNE1JSkoyeGpPcFIzZHVVRUo4SEhJVkVQRzZ0VmF3QTc2UmVZaTlUNmZNUUVWWm04WFpWeHpqOHRLMjZXQ2sxVkJ6QVp6SnBMaS9OOTd6dUdkTVZ0RUVZczBleUJtV3Arb0xhd1VzMTc3NUlVQkZ0OEtDRk1MZ3M2MnZNZ2tuVWFpT2N1SktrcG82OTlpWGd0azFZUzkxSENYL04zIiwiYWx0c2VjaWQiOiIxOmxpdmUuY29tOjAwMDM0MDAxMjQyNDIyRDMiLCJhbXIiOlsicHdkIiwibWZhIl0sImFwcGlkIjoiMDRiMDc3OTUtOGRkYi00NjFhLWJiZWUtMDJmOWUxYmY3YjQ2IiwiYXBwaWRhY3IiOiIwIiwiZW1haWwiOiJhcnR1cjcwMTUyQGdtYWlsLmNvbSIsImZhbWlseV9uYW1lIjoiZmVybmFuZGVzIiwiZ2l2ZW5fbmFtZSI6ImFydHVyIiwiZ3JvdXBzIjpbIjRmOGY5ZGQ2LTcyYzYtNDdjYy1iYmEyLTZhYmJkZWJlMzc3NyJdLCJpZHAiOiJsaXZlLmNvbSIsImlkdHlwIjoidXNlciIsImlwYWRkciI6IjI4MDQ6MTRjOjViYjE6OGIxMToyMjI0OjU0ZTM6YWIwNToxNTk3IiwibmFtZSI6ImFydHVyIGZlcm5hbmRlcyIsIm9pZCI6ImQ2NTZiOTRhLTQ1N2ItNDI2ZC1hYjVmLTUzMWMzNTE1OWQyMyIsInB1aWQiOiIxMDAzMjAwNDlBNkFGNDkwIiwicmgiOiIxLkFXTUJnMm1aZU9hUUdrNlJWajZrZl9KVUpGRFlQQkxmMmIxQWxOWEo4SHRfb2dQSUFhbGpBUS4iLCJzY3AiOiJ1c2VyX2ltcGVyc29uYXRpb24iLCJzaWQiOiIwMDUwZTcwOS0wODUyLThmOGMtNWI2Zi00NTM0YzM2NzdkZGIiLCJzdWIiOiJjQmdrSE5UTzJKTUs2ZXdVTFRDel9XU3VYV1I0Rm5VWjBtVEV5ZEUyVUJJIiwidGlkIjoiNzg5OTY5ODMtOTBlNi00ZTFhLTkxNTYtM2VhNDdmZjI1NDI0IiwidW5pcXVlX25hbWUiOiJsaXZlLmNvbSNhcnR1cjcwMTUyQGdtYWlsLmNvbSIsInV0aSI6Ikw5b2FfSTEydDBXOWhZNUp1cjRlQVEiLCJ2ZXIiOiIxLjAiLCJ4bXNfZnRkIjoiQlFRTkNPTlhTYWFvVVBvMWtzQTRUUDVxcHFkZnVndkNLeUNMdlN0X2FuUUJkWE51YjNKMGFDMWtjMjF6IiwieG1zX2lkcmVsIjoiMSAyNCJ9.VkcgQHboj6asRnAyJRRXHocp39vzd1tFhYIAoxJTVTL7S2-pit5MplccCyH1o-7TVIh-ojcX1jEcNcqZqH_imj1idoVOz-Hnt59beAgyVz0Txrn--zF8s6GcLb9aH2ZKjTF_HL0ShPAXWdQqPuvKg7ufBS4u-bhu18El42LcZyoxFbHOD_qUYfO-bcpYm1_DvpXJ22Zcqo-2pwGQrOgglRFh8XK2zxrXZANzzB1F5gEYt3ER0cw4Di-Gp0NlfeGTcJWBB2G8L88U_hJ0nq4eQHOcCCuPpqDw6uvpmqdXD-P8LbXasVg8HcZxbPO1HnS3wsUA-FtHSGuX0uPagQ0mkw"; 
        	 String password = "@Pucminas70152";
        Class.forName("org.postgresql.Driver");

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            String delSql = "DELETE FROM arte WHERE id = ?";
            try (PreparedStatement delStmt = conn.prepareStatement(delSql)) {
                delStmt.setInt(1, arteId);
                int affected = delStmt.executeUpdate();
                if (affected == 0) {
                    return new JSONObject().put("message", "Arte não encontrada.");
                }
            }
        }
        return new JSONObject().put("message", "Arte removida com sucesso.");
    }
    private static JSONObject addComment(int arteId, String usuario, String texto) throws Exception {
        String url = "jdbc:postgresql://pucmg5.postgres.database.azure.com:5432/postgres";
        String user = "adm"; // ou apenas "artur70152@ti270.postgres.database.azure.com" 
        	//private static final String password = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsIng1dCI6IkNOdjBPSTNSd3FsSEZFVm5hb01Bc2hDSDJYRSIsImtpZCI6IkNOdjBPSTNSd3FsSEZFVm5hb01Bc2hDSDJYRSJ9.eyJhdWQiOiJodHRwczovL29zc3JkYm1zLWFhZC5kYXRhYmFzZS53aW5kb3dzLm5ldCIsImlzcyI6Imh0dHBzOi8vc3RzLndpbmRvd3MubmV0Lzc4OTk2OTgzLTkwZTYtNGUxYS05MTU2LTNlYTQ3ZmYyNTQyNC8iLCJpYXQiOjE3NDg2MzgwNDQsIm5iZiI6MTc0ODYzODA0NCwiZXhwIjoxNzQ4NjQyNTU0LCJhY3IiOiIxIiwiYWlvIjoiQVpRQWEvOFpBQUFBZWhpNWVHVk5WM2NacmNmdWhZdWthU3Q3ZUFxWDVaenhmU25CNE1JSkoyeGpPcFIzZHVVRUo4SEhJVkVQRzZ0VmF3QTc2UmVZaTlUNmZNUUVWWm04WFpWeHpqOHRLMjZXQ2sxVkJ6QVp6SnBMaS9OOTd6dUdkTVZ0RUVZczBleUJtV3Arb0xhd1VzMTc3NUlVQkZ0OEtDRk1MZ3M2MnZNZ2tuVWFpT2N1SktrcG82OTlpWGd0azFZUzkxSENYL04zIiwiYWx0c2VjaWQiOiIxOmxpdmUuY29tOjAwMDM0MDAxMjQyNDIyRDMiLCJhbXIiOlsicHdkIiwibWZhIl0sImFwcGlkIjoiMDRiMDc3OTUtOGRkYi00NjFhLWJiZWUtMDJmOWUxYmY3YjQ2IiwiYXBwaWRhY3IiOiIwIiwiZW1haWwiOiJhcnR1cjcwMTUyQGdtYWlsLmNvbSIsImZhbWlseV9uYW1lIjoiZmVybmFuZGVzIiwiZ2l2ZW5fbmFtZSI6ImFydHVyIiwiZ3JvdXBzIjpbIjRmOGY5ZGQ2LTcyYzYtNDdjYy1iYmEyLTZhYmJkZWJlMzc3NyJdLCJpZHAiOiJsaXZlLmNvbSIsImlkdHlwIjoidXNlciIsImlwYWRkciI6IjI4MDQ6MTRjOjViYjE6OGIxMToyMjI0OjU0ZTM6YWIwNToxNTk3IiwibmFtZSI6ImFydHVyIGZlcm5hbmRlcyIsIm9pZCI6ImQ2NTZiOTRhLTQ1N2ItNDI2ZC1hYjVmLTUzMWMzNTE1OWQyMyIsInB1aWQiOiIxMDAzMjAwNDlBNkFGNDkwIiwicmgiOiIxLkFXTUJnMm1aZU9hUUdrNlJWajZrZl9KVUpGRFlQQkxmMmIxQWxOWEo4SHRfb2dQSUFhbGpBUS4iLCJzY3AiOiJ1c2VyX2ltcGVyc29uYXRpb24iLCJzaWQiOiIwMDUwZTcwOS0wODUyLThmOGMtNWI2Zi00NTM0YzM2NzdkZGIiLCJzdWIiOiJjQmdrSE5UTzJKTUs2ZXdVTFRDel9XU3VYV1I0Rm5VWjBtVEV5ZEUyVUJJIiwidGlkIjoiNzg5OTY5ODMtOTBlNi00ZTFhLTkxNTYtM2VhNDdmZjI1NDI0IiwidW5pcXVlX25hbWUiOiJsaXZlLmNvbSNhcnR1cjcwMTUyQGdtYWlsLmNvbSIsInV0aSI6Ikw5b2FfSTEydDBXOWhZNUp1cjRlQVEiLCJ2ZXIiOiIxLjAiLCJ4bXNfZnRkIjoiQlFRTkNPTlhTYWFvVVBvMWtzQTRUUDVxcHFkZnVndkNLeUNMdlN0X2FuUUJkWE51YjNKMGFDMWtjMjF6IiwieG1zX2lkcmVsIjoiMSAyNCJ9.VkcgQHboj6asRnAyJRRXHocp39vzd1tFhYIAoxJTVTL7S2-pit5MplccCyH1o-7TVIh-ojcX1jEcNcqZqH_imj1idoVOz-Hnt59beAgyVz0Txrn--zF8s6GcLb9aH2ZKjTF_HL0ShPAXWdQqPuvKg7ufBS4u-bhu18El42LcZyoxFbHOD_qUYfO-bcpYm1_DvpXJ22Zcqo-2pwGQrOgglRFh8XK2zxrXZANzzB1F5gEYt3ER0cw4Di-Gp0NlfeGTcJWBB2G8L88U_hJ0nq4eQHOcCCuPpqDw6uvpmqdXD-P8LbXasVg8HcZxbPO1HnS3wsUA-FtHSGuX0uPagQ0mkw"; 
        	 String password = "@Pucminas70152";

        Class.forName("org.postgresql.Driver");

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            String insSql = "INSERT INTO comentario_arte (arte_id, usuario, texto) VALUES (?, ?, ?)";
            try (PreparedStatement insStmt = conn.prepareStatement(insSql)) {
                insStmt.setInt(1, arteId);
                insStmt.setString(2, usuario);
                insStmt.setString(3, texto);
                insStmt.executeUpdate();
            }
        }
        return new JSONObject().put("message", "Comentário adicionado com sucesso.");
    }

    private static JSONArray getAllArtes() throws Exception {
        String url = "jdbc:postgresql://pucmg5.postgres.database.azure.com:5432/postgres";
        String user = "adm"; // ou apenas "artur70152@ti270.postgres.database.azure.com" 
        	//private static final String password = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsIng1dCI6IkNOdjBPSTNSd3FsSEZFVm5hb01Bc2hDSDJYRSIsImtpZCI6IkNOdjBPSTNSd3FsSEZFVm5hb01Bc2hDSDJYRSJ9.eyJhdWQiOiJodHRwczovL29zc3JkYm1zLWFhZC5kYXRhYmFzZS53aW5kb3dzLm5ldCIsImlzcyI6Imh0dHBzOi8vc3RzLndpbmRvd3MubmV0Lzc4OTk2OTgzLTkwZTYtNGUxYS05MTU2LTNlYTQ3ZmYyNTQyNC8iLCJpYXQiOjE3NDg2MzgwNDQsIm5iZiI6MTc0ODYzODA0NCwiZXhwIjoxNzQ4NjQyNTU0LCJhY3IiOiIxIiwiYWlvIjoiQVpRQWEvOFpBQUFBZWhpNWVHVk5WM2NacmNmdWhZdWthU3Q3ZUFxWDVaenhmU25CNE1JSkoyeGpPcFIzZHVVRUo4SEhJVkVQRzZ0VmF3QTc2UmVZaTlUNmZNUUVWWm04WFpWeHpqOHRLMjZXQ2sxVkJ6QVp6SnBMaS9OOTd6dUdkTVZ0RUVZczBleUJtV3Arb0xhd1VzMTc3NUlVQkZ0OEtDRk1MZ3M2MnZNZ2tuVWFpT2N1SktrcG82OTlpWGd0azFZUzkxSENYL04zIiwiYWx0c2VjaWQiOiIxOmxpdmUuY29tOjAwMDM0MDAxMjQyNDIyRDMiLCJhbXIiOlsicHdkIiwibWZhIl0sImFwcGlkIjoiMDRiMDc3OTUtOGRkYi00NjFhLWJiZWUtMDJmOWUxYmY3YjQ2IiwiYXBwaWRhY3IiOiIwIiwiZW1haWwiOiJhcnR1cjcwMTUyQGdtYWlsLmNvbSIsImZhbWlseV9uYW1lIjoiZmVybmFuZGVzIiwiZ2l2ZW5fbmFtZSI6ImFydHVyIiwiZ3JvdXBzIjpbIjRmOGY5ZGQ2LTcyYzYtNDdjYy1iYmEyLTZhYmJkZWJlMzc3NyJdLCJpZHAiOiJsaXZlLmNvbSIsImlkdHlwIjoidXNlciIsImlwYWRkciI6IjI4MDQ6MTRjOjViYjE6OGIxMToyMjI0OjU0ZTM6YWIwNToxNTk3IiwibmFtZSI6ImFydHVyIGZlcm5hbmRlcyIsIm9pZCI6ImQ2NTZiOTRhLTQ1N2ItNDI2ZC1hYjVmLTUzMWMzNTE1OWQyMyIsInB1aWQiOiIxMDAzMjAwNDlBNkFGNDkwIiwicmgiOiIxLkFXTUJnMm1aZU9hUUdrNlJWajZrZl9KVUpGRFlQQkxmMmIxQWxOWEo4SHRfb2dQSUFhbGpBUS4iLCJzY3AiOiJ1c2VyX2ltcGVyc29uYXRpb24iLCJzaWQiOiIwMDUwZTcwOS0wODUyLThmOGMtNWI2Zi00NTM0YzM2NzdkZGIiLCJzdWIiOiJjQmdrSE5UTzJKTUs2ZXdVTFRDel9XU3VYV1I0Rm5VWjBtVEV5ZEUyVUJJIiwidGlkIjoiNzg5OTY5ODMtOTBlNi00ZTFhLTkxNTYtM2VhNDdmZjI1NDI0IiwidW5pcXVlX25hbWUiOiJsaXZlLmNvbSNhcnR1cjcwMTUyQGdtYWlsLmNvbSIsInV0aSI6Ikw5b2FfSTEydDBXOWhZNUp1cjRlQVEiLCJ2ZXIiOiIxLjAiLCJ4bXNfZnRkIjoiQlFRTkNPTlhTYWFvVVBvMWtzQTRUUDVxcHFkZnVndkNLeUNMdlN0X2FuUUJkWE51YjNKMGFDMWtjMjF6IiwieG1zX2lkcmVsIjoiMSAyNCJ9.VkcgQHboj6asRnAyJRRXHocp39vzd1tFhYIAoxJTVTL7S2-pit5MplccCyH1o-7TVIh-ojcX1jEcNcqZqH_imj1idoVOz-Hnt59beAgyVz0Txrn--zF8s6GcLb9aH2ZKjTF_HL0ShPAXWdQqPuvKg7ufBS4u-bhu18El42LcZyoxFbHOD_qUYfO-bcpYm1_DvpXJ22Zcqo-2pwGQrOgglRFh8XK2zxrXZANzzB1F5gEYt3ER0cw4Di-Gp0NlfeGTcJWBB2G8L88U_hJ0nq4eQHOcCCuPpqDw6uvpmqdXD-P8LbXasVg8HcZxbPO1HnS3wsUA-FtHSGuX0uPagQ0mkw"; 
        	 String password = "@Pucminas70152";

        Class.forName("org.postgresql.Driver");

        JSONArray artesList = new JSONArray();

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
          //  String sql = "SELECT a.id as arte_id, a.arte, u.email, c.usuario as coment_usuario, c.texto as coment_texto " +
            //             "FROM arte a " +
            //             "JOIN usuario u ON a.usuario_id = u.id " +
           //              "LEFT JOIN comentario_arte c ON c.arte_id = a.id " +
            //             "ORDER BY a.id";

            
        	String sql = 
        		    "SELECT a.id as arte_id, a.arte, a.name, a.curtidas, a.descricao, u.email, " +
        		    "c.usuario as coment_usuario, c.texto as coment_texto " +
        		    "FROM arte a " +
        		    "JOIN usuario u ON a.usuario_id = u.id " +
        		    "LEFT JOIN comentario_arte c ON c.arte_id = a.id " +
        		    "ORDER BY a.id";



            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                try (ResultSet rs = stmt.executeQuery()) {
                    int lastArteId = -1;
                    JSONObject currentArte = null;
                    JSONArray comentarios = null;

                    while (rs.next()) {
                        int arteId = rs.getInt("arte_id");

                        // Nova arte
                        if (arteId != lastArteId) {
                            if (currentArte != null) {
                                currentArte.put("comentarios", comentarios);
                                artesList.put(currentArte);
                            }

                            currentArte = new JSONObject();
                            currentArte.put("id", arteId);
                            String caminhoImagem = rs.getString("arte");  // Ex: "image123.png"
                            
                            String urlBlob = "https://arturstorage123.blob.core.windows.net/upload/" + caminhoImagem;
                            System.out.println(urlBlob);
                            currentArte.put("arte", urlBlob);

                            currentArte.put("name", rs.getString("name"));
                            currentArte.put("usuario", rs.getString("email"));
                            currentArte.put("curtidas", rs.getInt("curtidas"));
                            currentArte.put("descricao", rs.getString("descricao")); // ✅ ADD ISSO

                            comentarios = new JSONArray();

                            lastArteId = arteId;
                        }

                        // Comentário (se houver)
                        String comentUsuario = rs.getString("coment_usuario");
                        String comentTexto = rs.getString("coment_texto");

                        if (comentUsuario != null && comentTexto != null) {
                            JSONObject comentario = new JSONObject();
                            comentario.put("usuario", comentUsuario);
                            comentario.put("texto", comentTexto);
                            comentarios.put(comentario);
                        }
                    }

                    // Última arte
                    if (currentArte != null) {
                        currentArte.put("comentarios", comentarios);
                        artesList.put(currentArte);
                    }
                }
            }
        }
        return artesList;
    }

    private static JSONObject updateProfilePhoto(String email, String foto) throws Exception {
        Class.forName("org.postgresql.Driver");
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            String sql = "UPDATE usuario SET foto = ? WHERE email = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, foto);
                stmt.setString(2, email);
                int affected = stmt.executeUpdate();
                if (affected == 0) {
                    return new JSONObject().put("message", "Usuário não encontrado.");
                }
            }
        }
        return new JSONObject().put("message", "Foto de perfil atualizada com sucesso.");
    }

    private static JSONArray getAllUsers() throws Exception {
        Class.forName("org.postgresql.Driver");
        JSONArray users = new JSONArray();
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            String sql = "SELECT * FROM usuario";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        JSONObject user = new JSONObject();
                        user.put("name", rs.getString("name"));
                        user.put("normal", rs.getBoolean("normal"));
                        user.put("doacoes", rs.getDouble("doacoes"));
                        user.put("email", rs.getString("email"));
                        user.put("senha", rs.getString("senha"));
                        user.put("foto", rs.getString("foto"));
                        users.put(user);
                    }
                }
            }
        }
        return users;
    }
    private static JSONArray getMessages(String email) throws Exception {
        Class.forName("org.postgresql.Driver");
        JSONArray mensagens = new JSONArray();
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            String sql = "SELECT usuario, mensagens, nome, foto FROM mensagem WHERE usuario = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, email);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        JSONObject msg = new JSONObject();
                        msg.put("usuario", rs.getString("usuario"));
                        msg.put("mensagens", rs.getString("mensagens"));
                        msg.put("nome", rs.getString("nome"));
                        msg.put("foto", rs.getString("foto"));
                        mensagens.put(msg);
                    }
                }
            }
        }
        return mensagens;
    }
    private static JSONObject addUser(String name, boolean normal, double doacoes, String email, String senha, String foto) throws Exception {
        Class.forName("org.postgresql.Driver");
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            String sql = "INSERT INTO usuario (name, normal, doacoes, email, senha, foto) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, name);
                stmt.setBoolean(2, normal);
                stmt.setDouble(3, doacoes);
                stmt.setString(4, email);
                stmt.setString(5, senha);
                stmt.setString(6, foto);
                stmt.executeUpdate();
            }
        }
        return new JSONObject().put("message", "Usuário salvo com sucesso!");
    }

}
