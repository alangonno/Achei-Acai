
package br.com.acheiacai.config;

import br.com.acheiacai.uteis.FabricaConexao; // Vamos reutilizar a lógica de conexão
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.flywaydb.core.Flyway;
import java.sql.Connection;

@WebListener
public class FlywayMigrationListener implements ServletContextListener {

    /**
     * Este método é chamado pelo Tomcat quando a aplicação está a iniciar.
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {

        try {

            String url = FabricaConexao.getDbUrl();
            String user = FabricaConexao.getDbUser();
            String password = FabricaConexao.getDbPassword();

            Flyway flyway = Flyway.configure()
                    .dataSource(url, user, password)
                    .schemas("public")
                    .load();

            flyway.migrate();

        } catch (Exception e) {
            System.err.println("!!! FALHA NA EXECUÇÃO DAS MIGRAÇÕES DO FLYWAY !!!");
            e.printStackTrace();
            throw new RuntimeException("Falha na migração do banco de dados.", e);
        }
    }




    @Override
    public void contextDestroyed(ServletContextEvent sce) {}
}