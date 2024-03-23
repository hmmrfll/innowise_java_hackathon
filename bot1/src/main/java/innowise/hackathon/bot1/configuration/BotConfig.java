package innowise.hackathon.bot1.configuration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Data
@AllArgsConstructor
@NoArgsConstructor
@PropertySource("classpath:application.properties")
public class BotConfig {
    @Value("${bot.username}")
    private String botUsername;

    @Value("${bot.token}")
    private String botToken;
}
