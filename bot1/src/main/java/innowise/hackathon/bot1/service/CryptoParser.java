package innowise.hackathon.bot1.service;

import com.google.gson.Gson;
import innowise.hackathon.bot1.entity.CryptoLast;
import innowise.hackathon.bot1.entity.CryptoNew;
import innowise.hackathon.bot1.repository.CryptoLastRepository;
import innowise.hackathon.bot1.repository.CryptoNewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Component
public class CryptoParser {

    @Autowired
    private CryptoLastRepository lastRepository;

    @Autowired
    private CryptoNewRepository newRepository;

    // Первоначальная загрузка данных и установка расписания для периодического обновления
    @Scheduled(fixedRate = 20000) // Выполнять каждые 20 секунд
    @Transactional
    public void parseAndStoreData() {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://api.mexc.com/api/v3/ticker/price";
        try {
            newRepository.deleteAll();
            String jsonResponse = restTemplate.getForObject(url, String.class);
            Gson gson = new Gson();
            CryptoNew[] cryptoNews = gson.fromJson(jsonResponse, CryptoNew[].class);
            for(CryptoNew cryptoNew : cryptoNews){
                CryptoNew entity = new CryptoNew();
                entity.setId(cryptoNew.getId());
                entity.setSymbol(cryptoNew.getSymbol());
                entity.setPrice(cryptoNew.getPrice());
                newRepository.save(entity);
            }
            lastRepository.deleteAll();
            for (CryptoNew cryptoNew : cryptoNews) {
                CryptoLast lastEntity = new CryptoLast();
                lastEntity.setId(cryptoNew.getId());
                lastEntity.setSymbol(cryptoNew.getSymbol());
                lastEntity.setPrice(cryptoNew.getPrice());
                lastRepository.save(lastEntity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

