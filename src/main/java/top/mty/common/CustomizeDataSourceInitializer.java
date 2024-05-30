package top.mty.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;

@Configuration
public class CustomizeDataSourceInitializer {
  @Value("classpath:sql/setup.sql")
  private Resource sqlScriptSchema;

  @Bean
  public DataSourceInitializer dataSourceInitializer(final DataSource dataSource) {
    DataSourceInitializer dataSourceInitializer = new DataSourceInitializer();
    dataSourceInitializer.setDataSource(dataSource);
    dataSourceInitializer.setDatabasePopulator(databasePopulator());
    return dataSourceInitializer;
  }
  
  private DatabasePopulator databasePopulator() {
    ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
    populator.addScript(sqlScriptSchema);
    populator.setSeparator(";");
    return populator;
  }
}
