package com.centroSer.app.api.user;

import com.centroSer.app.infra.security.SecurityContextDto;
import com.centroSer.app.persistent.entities.enums.UserRole;
import com.centroSer.app.persistent.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserRepository userRepository;

    @PostMapping("/info")
    public String info() {
        SecurityContextDto context = (SecurityContextDto) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var user = userRepository.findById(context.userId()).orElseThrow();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm 'de' dd/MM/yyyy");
        ZoneId zoneId = ZoneId.of("America/Sao_Paulo");

        if (context.role().equals(UserRole.ADMIN)) {
            StringBuilder report = new StringBuilder("Olá administrador, aqui está um relatório do último login de todos os pacientes do nosso centro:\n\n");

            StringBuilder noAccess = new StringBuilder("\nPacientes que se cadastraram mas não usaram nossos serviços:\n");

            userRepository.findAll().stream()
                    .filter(p -> !p.getId().equals(user.getId()))
                    .forEach(p -> {
                        if (p.getLastAccess() != null) {
                            ZonedDateTime lastAccess = p.getLastAccess().withZoneSameInstant(zoneId);
                            String formattedDate = lastAccess.format(formatter);
                            report.append(String.format("%s %s ---> %s\n", p.getUsername(), formattedDate, p.getEmail()));
                        } else {
                            noAccess.append(String.format("%s ---> %s\n", p.getUsername(), p.getEmail()));
                        }
                    });

            return report.append(noAccess).toString();
        }

        if (context.role().equals(UserRole.USER)) {
            if (user.getLastAccess() == null) {
                return String.format("Olá %s, como vai você?\nVocê ainda não acessou nossos serviços.", user.getUsername());
            }

            ZonedDateTime zonedDateTime = user.getLastAccess().withZoneSameInstant(zoneId);
            String formattedDate = zonedDateTime.format(formatter);

            return String.format("Olá %s, como vai você?\nEspero que tudo bem, seu último acesso foi às %s.",
                    user.getUsername(), formattedDate);
        }

        return "Acesso não autorizado.";
    }


}
