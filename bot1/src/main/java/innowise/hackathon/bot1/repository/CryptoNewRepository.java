package innowise.hackathon.bot1.repository;

import innowise.hackathon.bot1.entity.CryptoNew;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CryptoNewRepository extends JpaRepository<CryptoNew, Long> {
}
