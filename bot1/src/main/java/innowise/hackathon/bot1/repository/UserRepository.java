package innowise.hackathon.bot1.repository;


import innowise.hackathon.bot1.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
