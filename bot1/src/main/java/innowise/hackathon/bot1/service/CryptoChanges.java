package innowise.hackathon.bot1.service;

import innowise.hackathon.bot1.entity.CryptoLast;
import innowise.hackathon.bot1.entity.CryptoNew;
import org.springframework.stereotype.Component;
import innowise.hackathon.bot1.repository.CryptoNewRepository;
import innowise.hackathon.bot1.repository.CryptoLastRepository;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CryptoChanges {

    private final CryptoNewRepository newRepository;
    private final CryptoLastRepository lastRepository;
    private final CryptoParser cryptoParser;

    public List<String> infoChangesOfPercent(double percentChange) {
        cryptoParser.parseAndStoreData();
        List<String> changes = new ArrayList<>();

        // Получаем все записи из обоих репозиториев
        List<CryptoNew> newCryptoList = newRepository.findAll();
        List<CryptoLast> lastCryptoList = lastRepository.findAll();

        // Проходим по всем монетам из списка lastCryptoList
        for (CryptoLast lastCrypto : lastCryptoList) {
            // Находим монету с таким же символом в списке newCryptoList
            CryptoNew newCrypto = newCryptoList.stream()
                    .filter(crypto -> crypto.getSymbol().equals(lastCrypto.getSymbol()))
                    .findFirst()
                    .orElse(null);

            // Если монета найдена и есть изменение в цене
            if (newCrypto != null) {
                double lastPrice = lastCrypto.getPrice();
                double newPrice = newCrypto.getPrice();
                double change = ((newPrice - lastPrice) / lastPrice) * 100;

                // Если изменение в цене соответствует заданному проценту
                if (Math.abs(change) >= percentChange) {
                    String message = String.format("%s: %.2f%%", newCrypto.getSymbol(), change);
                    changes.add(message);
                }
            }
        }

        // Сортируем список по изменению цены
        changes.sort((s1, s2) -> {
            double change1 = Double.parseDouble(s1.split(": ")[1].replace("%", ""));
            double change2 = Double.parseDouble(s2.split(": ")[1].replace("%", ""));
            return Double.compare(change2, change1);
        });

        return changes;
    }

    public String formatChangesForTelegram(List<String> changes) {
        StringBuilder messageBuilder = new StringBuilder();

        // Проверяем, есть ли изменения в списке
        if (changes.isEmpty()) {
            messageBuilder.append("Нет изменений.");
        } else {
            // Перебираем все изменения и добавляем их в текстовую строку
            for (String change : changes) {
                messageBuilder.append(change).append("\n");
            }
        }
        // Возвращаем сформированную текстовую строку
        return messageBuilder.toString();
    }

}

